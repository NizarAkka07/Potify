<template>
  <q-page class="bg-grey-2 q-pa-md column overflow-hidden" style="height: calc(100vh - 60px);">
    <!-- Header Toolbar -->
    <div class="row items-center justify-between bg-white q-pa-sm rounded-borders shadow-1 q-mb-md">
      <div class="row items-center q-gutter-x-sm">
        <q-icon name="headset_mic" size="32px" color="primary" />
        <div>
          <div class="text-h6 text-bold text-grey-9 leading-tight">Centre de Support Administrateur</div>
          <div class="text-caption text-grey-7">Gestion des tickets en attente, transferts et réponses en temps réel</div>
        </div>
      </div>
      <div class="row items-center q-gutter-x-sm">
        <q-chip color="deep-orange-1" text-color="deep-orange-9" icon="pending_actions" class="text-bold">
          {{ pendingConversations.length }} en attente
        </q-chip>
        <q-chip color="blue-1" text-color="blue-9" icon="question_answer" class="text-bold">
          {{ assignedConversations.length }} mes tickets
        </q-chip>
        <q-btn icon="refresh" flat round color="grey-8" @click="loadAllData" />
      </div>
    </div>

    <!-- Main Workspace Split -->
    <div class="row col q-col-gutter-md overflow-hidden">
      <!-- Left Column: Queue & Ticket Tabs -->
      <div class="col-12 col-md-4 column">
        <q-card class="col column shadow-2 rounded-borders overflow-hidden">
          <q-tabs v-model="activeTab" dense class="text-grey-7 bg-grey-1" active-color="primary" indicator-color="primary" align="justify">
            <q-tab name="pending" icon="pending" label="En Attente">
              <q-badge v-if="pendingConversations.length > 0" color="red" floating>{{ pendingConversations.length }}</q-badge>
            </q-tab>
            <q-tab name="assigned" icon="forum" label="Mes Tickets">
              <q-badge v-if="assignedConversations.length > 0" color="blue" floating>{{ assignedConversations.length }}</q-badge>
            </q-tab>
          </q-tabs>

          <q-separator />

          <q-card-section class="col overflow-auto q-pa-none">
            <!-- Pending Tickets List -->
            <q-list v-if="activeTab === 'pending'" separator>
              <q-item v-if="pendingConversations.length === 0" class="text-center text-grey-6 q-pa-lg">
                <q-item-section>Aucun ticket en attente dans la file</q-item-section>
              </q-item>

              <q-item
                v-for="conv in pendingConversations"
                :key="conv.id"
                clickable
                :active="selectedConversation?.id === conv.id"
                active-class="bg-blue-1 text-primary text-bold"
                @click="selectConversation(conv)"
              >
                <q-item-section avatar>
                  <q-avatar color="deep-orange-2" text-color="deep-orange-9" icon="person" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-bold">{{ conv.userName }}</q-item-label>
                  <q-item-label caption class="ellipsis">{{ conv.userEmail }}</q-item-label>
                  <q-item-label caption class="text-deep-orange-8 text-bold">File d'attente</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-btn size="sm" color="primary" label="Rejoindre" icon="login" @click.stop="assignToMe(conv.id)" />
                </q-item-section>
              </q-item>
            </q-list>

            <!-- Assigned Tickets List -->
            <q-list v-if="activeTab === 'assigned'" separator>
              <q-item v-if="assignedConversations.length === 0" class="text-center text-grey-6 q-pa-lg">
                <q-item-section>Aucun ticket actif assigné</q-item-section>
              </q-item>

              <q-item
                v-for="conv in assignedConversations"
                :key="conv.id"
                clickable
                :active="selectedConversation?.id === conv.id"
                active-class="bg-blue-1 text-primary text-bold"
                @click="selectConversation(conv)"
              >
                <q-item-section avatar>
                  <q-avatar color="primary" text-color="white" icon="person" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-bold">{{ conv.userName }}</q-item-label>
                  <q-item-label caption class="ellipsis">{{ conv.userEmail }}</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <span class="text-caption text-grey-6">{{ formatTime(conv.lastMessageAt) }}</span>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card-section>
        </q-card>
      </div>

      <!-- Right Column: Conversation Thread & Actions -->
      <div class="col-12 col-md-8 column">
        <q-card v-if="selectedConversation" class="col column shadow-2 rounded-borders overflow-hidden">
          <!-- Active Conversation Header -->
          <q-card-section class="bg-primary text-white q-py-sm row items-center justify-between">
            <div class="row items-center q-gutter-x-sm">
              <q-avatar color="white" text-color="primary" icon="person" size="40px" />
              <div>
                <div class="text-subtitle1 text-bold">{{ selectedConversation.userName }}</div>
                <div class="text-caption text-grey-3">{{ selectedConversation.userEmail }}</div>
              </div>
            </div>
            <div class="row items-center q-gutter-x-xs">
              <q-btn
                v-if="selectedConversation.status === 'PENDING_AGENT'"
                color="positive"
                label="S'assigner la conversation"
                icon="check_circle"
                dense
                class="q-px-sm"
                @click="assignToMe(selectedConversation.id)"
              />
              <template v-else>
                <q-btn color="amber-9" label="Transférer" icon="swap_horiz" dense flat class="text-bold" @click="openTransferModal" />
                <q-btn color="negative" label="Quitter" icon="logout" dense flat class="text-bold" @click="leaveCurrentConversation" />
                <q-btn color="positive" label="Résoudre" icon="task_alt" dense class="q-px-sm" @click="resolveCurrentConversation" />
              </template>
            </div>
          </q-card-section>

          <!-- Chat Thread Container -->
          <q-card-section ref="threadContainer" class="col overflow-auto q-pa-md bg-grey-2">
            <div v-for="msg in currentMessages" :key="msg.id" class="q-mb-sm">
              <div v-if="msg.senderType === 'SYSTEM'" class="text-center q-my-sm q-px-sm">
                <span class="inline-block bg-grey-3 text-grey-9 text-caption rounded-borders q-px-md q-py-xs shadow-1" style="white-space: normal; word-break: break-word; max-width: 90%;">
                  {{ msg.content }}
                </span>
              </div>
              <q-chat-message
                v-else
                :name="msg.senderName"
                :text="[msg.content || '']"
                :stamp="formatTime(msg.createdAt)"
                :sent="msg.senderType === 'ADMIN'"
                :bg-color="msg.senderType === 'ADMIN' ? 'primary' : 'white'"
                :text-color="msg.senderType === 'ADMIN' ? 'white' : 'dark'"
              >
                <template #avatar>
                  <q-avatar
                    size="36px"
                    :class="[
                      msg.senderType === 'ADMIN' ? 'q-ml-sm bg-primary' : (msg.senderType === 'BOT' ? 'q-mr-sm bg-teal-6' : 'q-mr-sm bg-deep-orange-6')
                    ]"
                    text-color="white"
                    class="shadow-1"
                  >
                    <q-icon :name="getAdminAvatarIcon(msg.senderType)" size="22px" />
                  </q-avatar>
                </template>
              </q-chat-message>
            </div>
          </q-card-section>

          <!-- Canned Responses Shortcuts Bar -->
          <div v-if="cannedResponses.length > 0" class="bg-grey-3 q-px-sm q-py-xs row items-center q-gutter-x-xs overflow-auto">
            <span class="text-caption text-bold text-grey-7">Réponses rapides :</span>
            <q-chip
              v-for="resp in cannedResponses"
              :key="resp.id"
              clickable
              dense
              color="white"
              text-color="primary"
              class="shadow-1"
              @click="insertCannedResponse(resp.content)"
            >
              {{ resp.shortcut }}
            </q-chip>
          </div>

          <!-- Input Controls Footer -->
          <q-card-section class="bg-white q-pa-xs border-top">
            <q-form class="row items-center no-wrap" @submit.prevent="sendAdminReply">
              <q-input
                v-model="replyText"
                dense
                outlined
                placeholder="Écrire une réponse..."
                class="col"
                :disable="selectedConversation.status === 'PENDING_AGENT'"
                @keydown.enter.prevent="sendAdminReply"
              >
                <template #after>
                  <q-btn
                    round
                    flat
                    color="primary"
                    icon="send"
                    :disable="!replyText.trim() || selectedConversation.status === 'PENDING_AGENT'"
                    type="submit"
                  />
                </template>
              </q-input>
            </q-form>
          </q-card-section>
        </q-card>

        <!-- No Selection Placeholder -->
        <q-card v-else class="col column justify-center items-center text-grey-6 bg-white shadow-2 rounded-borders">
          <q-icon name="chat_bubble_outline" size="64px" color="grey-4" />
          <div class="text-h6 q-mt-md">Sélectionnez une conversation</div>
          <div class="text-caption">Choisissez un ticket dans la file d'attente pour démarrer l'assistance</div>
        </q-card>
      </div>
    </div>

    <!-- Transfer Dialog -->
    <q-dialog v-model="isTransferOpen">
      <q-card style="width: 400px;" class="q-pa-md">
        <q-card-section class="text-h6">Transférer la conversation</q-card-section>
        <q-card-section>
          <q-input v-model="targetAdminId" label="ID de l'administrateur cible" outlined dense class="q-mb-md" />
          <q-input v-model="transferReason" label="Motif du transfert" type="textarea" outlined dense />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Annuler" v-close-popup />
          <q-btn color="primary" label="Transférer" @click="confirmTransfer" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import {
  getPendingConversationsAdmin,
  getAssignedConversationsAdmin,
  assignConversationAdmin,
  sendMessageAdmin,
  transferConversationAdmin,
  leaveConversationAdmin,
  resolveConversationAdmin,
  getMessagesAdmin,
  getCannedResponsesAdmin,
  sendAdminHeartbeat
} from 'src/shared/services/supportService'

const $q = useQuasar()
const activeTab = ref('pending')
const pendingConversations = ref([])
const assignedConversations = ref([])
const selectedConversation = ref(null)
const currentMessages = ref([])
const cannedResponses = ref([])
const replyText = ref('')
const threadContainer = ref(null)

const isTransferOpen = ref(false)
const targetAdminId = ref('')
const transferReason = ref('')

const loadAllData = async () => {
  try {
    sendAdminHeartbeat().catch(() => {})
    const [pendingRes, assignedRes, cannedRes] = await Promise.all([
      getPendingConversationsAdmin(),
      getAssignedConversationsAdmin(),
      getCannedResponsesAdmin()
    ])
    pendingConversations.value = pendingRes.data
    assignedConversations.value = assignedRes.data
    cannedResponses.value = cannedRes.data
  } catch (err) {
    console.error('Erreur chargement support dashboard:', err)
  }
}

const selectConversation = async (conv) => {
  selectedConversation.value = conv
  await loadMessages(conv.id)
}

const loadMessages = async (convId) => {
  try {
    const res = await getMessagesAdmin(convId)
    currentMessages.value = res.data.reverse()
    scrollToBottom()
  } catch (err) {
    console.error('Erreur chargement messages:', err)
  }
}

const assignToMe = async (convId) => {
  try {
    const res = await assignConversationAdmin(convId)
    $q.notify({ type: 'positive', message: 'Ticket assigné avec succès !' })
    await loadAllData()
    selectedConversation.value = res.data
    await loadMessages(convId)
    activeTab.value = 'assigned'
  } catch (err) {
    console.error('Erreur assignation:', err)
    $q.notify({ type: 'negative', message: 'Échec de l\'assignation' })
  }
}

const sendAdminReply = async () => {
  const text = replyText.value.trim()
  if (!text || !selectedConversation.value) return

  replyText.value = ''
  try {
    const res = await sendMessageAdmin({
      conversationId: selectedConversation.value.id,
      content: text
    })
    currentMessages.value.push(res.data)
    scrollToBottom()
  } catch (err) {
    console.error('Erreur envoi réponse admin:', err)
    $q.notify({ type: 'negative', message: 'Erreur d\'envoi de la réponse' })
  }
}

const openTransferModal = () => {
  targetAdminId.value = ''
  transferReason.value = ''
  isTransferOpen.value = true
}

const confirmTransfer = async () => {
  if (!targetAdminId.value || !selectedConversation.value) return
  try {
    await transferConversationAdmin(selectedConversation.value.id, {
      targetAdminId: targetAdminId.value,
      reason: transferReason.value
    })
    $q.notify({ type: 'positive', message: 'Ticket transféré avec succès.' })
    isTransferOpen.value = false
    selectedConversation.value = null
    await loadAllData()
  } catch (err) {
    console.error('Erreur transfert:', err)
    $q.notify({ type: 'negative', message: 'Erreur lors du transfert.' })
  }
}

const leaveCurrentConversation = async () => {
  if (!selectedConversation.value) return
  try {
    await leaveConversationAdmin(selectedConversation.value.id)
    $q.notify({ type: 'info', message: 'Vous avez quitté la conversation.' })
    selectedConversation.value = null
    await loadAllData()
  } catch (err) {
    console.error('Erreur quitter conversation:', err)
    $q.notify({ type: 'negative', message: 'Erreur pour quitter.' })
  }
}

const resolveCurrentConversation = async () => {
  if (!selectedConversation.value) return
  try {
    await resolveConversationAdmin(selectedConversation.value.id)
    $q.notify({ type: 'positive', message: 'Conversation marquée comme résolue.' })
    selectedConversation.value = null
    await loadAllData()
  } catch (err) {
    console.error('Erreur résolution conversation:', err)
    $q.notify({ type: 'negative', message: 'Erreur lors de la résolution.' })
  }
}

const getAdminAvatarIcon = (type) => {
  if (type === 'ADMIN') return 'support_agent'
  if (type === 'BOT') return 'smart_toy'
  return 'person'
}

const insertCannedResponse = (content) => {
  replyText.value = content
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (threadContainer.value) {
      threadContainer.value.scrollTop = threadContainer.value.scrollHeight
    }
  })
}

let pollTimer = null

onMounted(() => {
  loadAllData()
  pollTimer = setInterval(() => {
    loadAllData()
    if (selectedConversation.value) {
      loadMessages(selectedConversation.value.id)
    }
  }, 3000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped>
.border-top {
  border-top: 1px solid #e0e0e0;
}

:deep(.q-message-text-content) {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
