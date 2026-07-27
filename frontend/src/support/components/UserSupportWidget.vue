<template>
  <div class="user-support-widget">
    <!-- Floating Launcher Button -->
    <q-btn
      round
      color="primary"
      icon="chat"
      size="lg"
      class="shadow-10 pulse-badge-btn floating-chat-btn"
      @click="toggleChat"
    >
      <q-badge v-if="unreadCount > 0" color="red" floating class="text-bold">
        {{ unreadCount }}
      </q-badge>
    </q-btn>

    <!-- Chat Dialog Window -->
    <q-dialog v-model="isOpen" position="bottom" seamless class="chat-dialog">
      <q-card style="width: 380px; max-width: 95vw; height: 550px;" class="column bg-grey-1 shadow-24 rounded-borders overflow-hidden q-ml-auto q-mr-lg q-mb-lg">
        <!-- Header -->
        <q-card-section class="bg-primary text-white q-py-sm row items-center justify-between shadow-2">
          <div class="row items-center q-gutter-x-sm">
            <q-avatar size="36px" class="bg-white text-primary text-bold shadow-1">
              <q-icon :name="isHumanAssigned ? 'support_agent' : 'smart_toy'" />
            </q-avatar>
            <div>
              <div class="text-subtitle1 text-bold leading-tight">
                {{ isHumanAssigned ? 'Support Client Admin' : 'Assistant IA Potify' }}
              </div>
              <div class="text-caption text-grey-3 row items-center q-gutter-x-xs">
                <span class="status-dot" :class="isHumanAssigned ? 'bg-positive' : 'bg-info'"></span>
                <span>{{ statusText }}</span>
              </div>
            </div>
          </div>
          <div>
            <q-btn flat round dense icon="close" @click="isOpen = false" />
          </div>
        </q-card-section>

        <!-- Messages Body -->
        <q-card-section ref="messagesContainer" class="col overflow-auto q-pa-md bg-grey-2">
          <div v-for="msg in messages" :key="msg.id" class="q-mb-sm">
            <!-- System Event Message -->
            <div v-if="msg.senderType === 'SYSTEM'" class="text-center q-my-xs q-px-xs">
              <span class="inline-block bg-grey-3 text-grey-9 text-caption rounded-borders q-px-sm q-py-xs shadow-1" style="white-space: normal; word-break: break-word; max-width: 95%;">
                {{ msg.content }}
              </span>
            </div>

            <!-- User / Bot / Admin Messages -->
            <q-chat-message
              v-else
              :name="msg.senderName"
              :text="[msg.content || '']"
              :stamp="formatTime(msg.createdAt)"
              :sent="msg.senderType === 'USER'"
              :bg-color="getBgColor(msg.senderType)"
              :text-color="msg.senderType === 'USER' ? 'white' : 'dark'"
              class="q-mb-xs"
            >
              <template #avatar>
                <q-avatar
                  size="34px"
                  :class="[
                    msg.senderType === 'USER' ? 'q-ml-sm' : 'q-mr-sm',
                    getAvatarBgClass(msg.senderType)
                  ]"
                  text-color="white"
                  class="shadow-1"
                >
                  <q-icon :name="getAvatarIcon(msg.senderType)" size="20px" />
                </q-avatar>
              </template>

              <template v-if="msg.attachmentUrl" #default>
                <div>
                  <q-img :src="msg.attachmentUrl" style="max-width: 200px; border-radius: 8px;" class="q-mb-xs" />
                  <div>{{ msg.content }}</div>
                </div>
              </template>
            </q-chat-message>
          </div>

          <!-- Typing Indicator -->
          <div v-if="isTyping" class="row items-center q-gutter-x-xs text-grey-6 text-caption q-ml-sm q-mb-sm">
            <q-spinner-dots size="20px" color="primary" />
            <span>Votre interlocuteur écrit...</span>
          </div>
        </q-card-section>

        <!-- Escalation Banner if BOT_ACTIVE -->
        <div v-if="conversation?.status === 'BOT_ACTIVE'" class="bg-amber-1 q-px-sm q-py-xs row items-center justify-between border-top border-amber-3">
          <span class="text-caption text-amber-9 text-bold">Besoin d'un conseiller humain ?</span>
          <q-btn dense size="sm" color="amber-10" label="Parler à un humain" icon-right="headset_mic" flat @click="requestHuman" />
        </div>

        <!-- Rating Prompt Banner if RESOLVED -->
        <div v-if="conversation?.status === 'RESOLVED'" class="bg-positive text-white q-pa-sm text-center">
          <div class="text-caption text-bold q-mb-xs">Le problème est-il résolu ? Évaluez notre support :</div>
          <q-rating v-model="ratingScore" size="24px" color="white" class="q-mb-xs" />
          <q-btn size="sm" color="white" text-color="positive" label="Envoyer mon avis" class="q-ml-sm" @click="submitCSAT" />
        </div>

        <!-- Message Input Footer -->
        <q-card-section v-if="conversation?.status !== 'CLOSED'" class="bg-white q-pa-xs border-top">
          <q-form class="row items-center no-wrap" @submit.prevent="handleSend">
            <q-input
              v-model="inputContent"
              dense
              outlined
              placeholder="Écrivez votre message..."
              class="col"
              maxlength="1000"
              @keydown.enter.prevent="handleSend"
            >
              <template #after>
                <q-btn
                  round
                  flat
                  color="primary"
                  icon="send"
                  :disable="!inputContent.trim()"
                  type="submit"
                />
              </template>
            </q-input>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import {
  getActiveConversation,
  sendMessageUser,
  escalateConversation,
  getMessagesUser,
  submitRating
} from 'src/shared/services/supportService'

const $q = useQuasar()
const isOpen = ref(false)
const conversation = ref(null)
const messages = ref([])
const inputContent = ref('')
const isTyping = ref(false)
const unreadCount = ref(0)
const ratingScore = ref(5)
const messagesContainer = ref(null)

const isHumanAssigned = computed(() => conversation.value?.status === 'AGENT_ASSIGNED')

const statusText = computed(() => {
  if (!conversation.value) return 'En ligne'
  switch (conversation.value.status) {
    case 'BOT_ACTIVE': return 'Réponse instantanée par IA'
    case 'PENDING_AGENT': return 'En recherche d\'un conseiller...'
    case 'AGENT_ASSIGNED': return `En ligne avec ${conversation.value.assignedAdminName || 'Agent'}`
    case 'RESOLVED': return 'Conversation résolue'
    case 'CLOSED': return 'Conversation fermée'
    default: return 'Support'
  }
})

const toggleChat = async () => {
  isOpen.value = !isOpen.value
  if (isOpen.value && !conversation.value) {
    await initConversation()
  }
}

const initConversation = async () => {
  try {
    const res = await getActiveConversation()
    conversation.value = res.data
    await fetchMessages()
  } catch (err) {
    console.error('Erreur initialisation du support:', err)
  }
}

const fetchMessages = async () => {
  if (!conversation.value) return
  try {
    const res = await getMessagesUser(conversation.value.id)
    messages.value = res.data.reverse()
    scrollToBottom()
  } catch (err) {
    console.error('Erreur chargement messages:', err)
  }
}

const handleSend = async () => {
  const text = inputContent.value.trim()
  if (!text || !conversation.value) return

  inputContent.value = ''
  try {
    const res = await sendMessageUser({
      conversationId: conversation.value.id,
      content: text
    })
    messages.value.push(res.data)
    scrollToBottom()
    // Re-sync after short delay for bot reply
    setTimeout(fetchMessages, 800)
  } catch (err) {
    console.error('Erreur envoi message:', err)
    $q.notify({ type: 'negative', message: 'Erreur lors de l\'envoi du message' })
  }
}

const requestHuman = async () => {
  if (!conversation.value) return
  try {
    await escalateConversation(conversation.value.id)
    conversation.value.status = 'PENDING_AGENT'
    await fetchMessages()
    $q.notify({ type: 'info', message: 'Transfert vers un administrateur demandé.' })
  } catch (err) {
    console.error('Erreur escalade:', err)
    $q.notify({ type: 'negative', message: 'Échec de la demande d\'escalade.' })
  }
}

const submitCSAT = async () => {
  if (!conversation.value) return
  try {
    await submitRating(conversation.value.id, {
      score: ratingScore.value,
      comment: 'Évaluation via widget chat'
    })
    conversation.value.status = 'CLOSED'
    await fetchMessages()
    $q.notify({ type: 'positive', message: 'Merci pour votre évaluation !' })
  } catch (err) {
    console.error('Erreur évaluation CSAT:', err)
    $q.notify({ type: 'negative', message: 'Erreur lors de la soumission.' })
  }
}

const getBgColor = (type) => {
  if (type === 'USER') return 'primary'
  if (type === 'BOT') return 'grey-3'
  if (type === 'ADMIN') return 'blue-2'
  return 'white'
}

const getAvatarIcon = (type) => {
  if (type === 'USER') return 'person'
  if (type === 'BOT') return 'smart_toy'
  if (type === 'ADMIN') return 'support_agent'
  return 'forum'
}

const getAvatarBgClass = (type) => {
  if (type === 'USER') return 'bg-primary'
  if (type === 'BOT') return 'bg-teal-6'
  if (type === 'ADMIN') return 'bg-deep-orange-6'
  return 'bg-grey-7'
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

import authStore from 'src/shared/stores/auth'
import { watch, onUnmounted } from 'vue'

let pollTimer = null

const startPolling = () => {
  stopPolling()
  pollTimer = setInterval(() => {
    if (isOpen.value && conversation.value) {
      fetchMessages()
    }
  }, 3000)
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

watch(
  () => isOpen.value,
  (val) => {
    if (val) {
      startPolling()
    } else {
      stopPolling()
    }
  }
)

watch(
  () => authStore.isAuthenticated.value,
  async () => {
    conversation.value = null
    messages.value = []
    if (isOpen.value) {
      await initConversation()
    }
  }
)

onUnmounted(() => {
  stopPolling()
})

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.user-support-widget {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
}

.floating-chat-btn {
  transition: transform 0.2s ease-in-out;
}
.floating-chat-btn:hover {
  transform: scale(1.08);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.border-top {
  border-top: 1px solid #e0e0e0;
}

:deep(.q-message-text-content) {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
