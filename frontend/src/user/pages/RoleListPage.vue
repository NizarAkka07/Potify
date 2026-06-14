<template>
  <q-page padding style="background: #F8F9FA;">
    <!-- Welcome Header -->
    <div class="row items-center justify-between q-mb-xl q-pa-lg text-white" style="background: linear-gradient(135deg, #0D1B2E 0%, #1A2E40 100%); border-radius: 16px; border-left: 6px solid #FFB300;">
      <div>
        <div class="text-h4 text-weight-bold">{{ $t('roleList.title') }}</div>
        <div class="text-subtitle1 text-grey-4 q-mt-xs">Gérez la sécurité et les accès en glissant-déposant les permissions disponibles sur les rôles.</div>
      </div>
      <div class="row q-gutter-sm">
        <q-btn color="amber-8" text-color="dark" icon="add" :label="$t('roleList.newRoleButton')" @click="openRoleDialog()" no-caps class="text-weight-bold" />
        <q-btn outline color="white" icon="vpn_key" :label="$t('roleList.newPermissionButton')" @click="openPermissionDialog()" no-caps />
      </div>
    </div>

    <div class="row q-col-gutter-lg">
      <!-- Left Column: Available Permissions -->
      <div class="col-12 col-md-4">
        <q-card class="shadow-2" style="border-radius: 12px; height: 100%;">
          <q-card-section class="q-pb-none">
            <div class="text-subtitle1 text-weight-bold text-dark q-mb-md">
              <q-icon name="vpn_key" color="primary" class="q-mr-sm" size="sm" />
              {{ $t('roleList.availablePermissions') }}
            </div>
            <q-input outlined dense v-model="searchQuery" :placeholder="$t('roleList.searchPlaceholder')" color="secondary" class="q-mb-md">
              <template v-slot:append>
                <q-icon name="search" />
              </template>
            </q-input>
          </q-card-section>

          <q-card-section style="max-height: 600px; overflow-y: auto;">
            <div v-if="loadingPermissions" class="flex flex-center q-py-xl">
              <q-spinner-dots size="40px" color="primary" />
            </div>
            
            <q-list v-else class="q-gutter-y-sm">
              <q-item
                v-for="perm in filteredPermissions"
                :key="perm.id"
                draggable="true"
                @dragstart="onDragStart($event, perm)"
                class="perm-item q-pa-md rounded-borders bg-grey-1 cursor-grab"
                style="border: 1px solid rgba(0,0,0,0.06);"
              >
                <q-item-section avatar class="q-pr-none" style="min-width: 32px;">
                  <q-icon name="drag_indicator" color="grey-5" />
                </q-item-section>

                <q-item-section>
                  <q-item-label class="text-weight-bold text-primary">{{ perm.code }}</q-item-label>
                  <q-item-label caption class="line-clamp-2">{{ perm.description || $t('roleList.noDescription') }}</q-item-label>
                </q-item-section>

                <q-item-section side>
                  <q-btn flat round dense color="grey-6" icon="delete" size="sm" @click="confirmDeletePermission(perm)">
                    <q-tooltip>{{ $t('roleList.deletePermissionTooltip') }}</q-tooltip>
                  </q-btn>
                </q-item-section>
              </q-item>

              <div v-if="filteredPermissions.length === 0" class="text-center q-py-xl text-grey-6 text-italic">
                {{ $t('roleList.noPermissionsFound') }}
              </div>
            </q-list>
          </q-card-section>
        </q-card>
      </div>

      <!-- Right Column: Active Roles List -->
      <div class="col-12 col-md-8">
        <div class="text-subtitle1 text-weight-bold text-dark q-mb-md">
          <q-icon name="security" color="primary" class="q-mr-sm" size="sm" />
          {{ $t('roleList.activeRoles') }}
        </div>

        <div v-if="loadingRoles" class="flex flex-center q-py-xl">
          <q-spinner-dots size="50px" color="primary" />
        </div>

        <div v-else class="row q-col-gutter-md">
          <div v-for="role in roles" :key="role.id" class="col-12 col-sm-6">
            <q-card 
              class="role-card shadow-2"
              :class="{ 'drag-over': activeDragRoleId === role.id }"
              @dragover.prevent="onDragOver(role.id)"
              @dragleave="onDragLeave(role.id)"
              @drop="onDrop($event, role)"
              style="border-radius: 12px; min-height: 250px; display: flex; flex-direction: column;"
            >
              <!-- Card Header with elegant gradient -->
              <div class="q-pa-md text-white row items-center justify-between" :style="getRoleHeaderStyle(role.name)">
                <div>
                  <div class="text-h6 text-weight-bold">{{ role.name }}</div>
                  <div class="text-caption text-grey-3" style="font-size: 0.8rem;">{{ role.description }}</div>
                </div>
                <div class="row q-gutter-xs">
                  <q-btn flat round dense icon="edit" color="white" size="sm" @click="openRoleDialog(role)">
                    <q-tooltip>{{ $t('roleList.editRoleTooltip') }}</q-tooltip>
                  </q-btn>
                  <q-btn flat round dense icon="delete" color="white" size="sm" @click="confirmDeleteRole(role)">
                    <q-tooltip>{{ $t('roleList.deleteRoleTooltip') }}</q-tooltip>
                  </q-btn>
                </div>
              </div>

              <!-- Draggable drop instruction / current permission list -->
              <q-card-section class="col q-pa-md flex-grow-1" style="background: white;">
                <div class="text-subtitle2 text-grey-7 q-mb-sm text-weight-medium">{{ $t('roleList.associatedPermissions') }} :</div>
                
                <div class="row q-col-gutter-xs q-mt-xs">
                  <div v-for="perm in role.permissions" :key="perm.id" class="col-auto">
                    <q-chip 
                      removable 
                      @remove="removePermission(role, perm)"
                      color="blue-1" 
                      text-color="blue-9" 
                      class="text-weight-bold" 
                      size="sm"
                    >
                      {{ perm.code }}
                    </q-chip>
                  </div>
                </div>

                <!-- Dropzone Placeholder when empty -->
                <div 
                  v-if="!role.permissions || role.permissions.length === 0" 
                  class="flex flex-center q-pa-lg text-grey-5 text-center text-italic"
                  style="border: 2px dashed rgba(0,0,0,0.06); border-radius: 8px; min-height: 100px;"
                >
                  {{ $t('roleList.dragPermissionsHint') }}
                </div>

                <!-- Hint when dragging over -->
                <div 
                  v-if="activeDragRoleId === role.id" 
                  class="absolute-full flex flex-center text-amber-9 text-weight-bold"
                  style="background: rgba(255, 179, 0, 0.05); border-radius: 12px; pointer-events: none;"
                >
                  <div class="column items-center">
                    <q-icon name="add_circle" size="md" />
                    <span class="q-mt-xs">{{ $t('roleList.dropToAdd') }}</span>
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { 
  getRoles, 
  deleteRole, 
  getPermissions, 
  deletePermission,
  assignPermissionToRole, 
  removePermissionFromRole 
} from 'src/shared/services/api'
import RoleFormDialog from 'src/user/components/RoleFormDialog.vue'
import PermissionFormDialog from 'src/user/components/PermissionFormDialog.vue'

const $q = useQuasar()

const roles = ref([])
const permissions = ref([])
const loadingRoles = ref(false)
const loadingPermissions = ref(false)
const searchQuery = ref('')
const activeDragRoleId = ref(null)

const filteredPermissions = computed(() => {
  if (!searchQuery.value) return permissions.value
  const query = searchQuery.value.toLowerCase()
  return permissions.value.filter(p => 
    p.code.toLowerCase().includes(query) || 
    (p.description && p.description.toLowerCase().includes(query))
  )
})

async function loadData() {
  loadingRoles.value = true
  loadingPermissions.value = true
  try {
    const [rolesResponse, permissionsResponse] = await Promise.all([
      getRoles(),
      getPermissions()
    ])
    roles.value = rolesResponse.data
    permissions.value = permissionsResponse.data
  } catch (error) {
    console.error('Erreur lors du chargement des données', error)
    $q.notify({ 
      type: 'negative', 
      message: 'Erreur lors du chargement de la sécurité' 
    })
  } finally {
    loadingRoles.value = false
    loadingPermissions.value = false
  }
}

// Drag & Drop Handling
function onDragStart(event, permission) {
  event.dataTransfer.setData('text/plain', JSON.stringify(permission))
  event.dataTransfer.effectAllowed = 'copy'
}

function onDragOver(roleId) {
  activeDragRoleId.value = roleId
}

function onDragLeave(roleId) {
  if (activeDragRoleId.value === roleId) {
    activeDragRoleId.value = null
  }
}

async function onDrop(event, role) {
  activeDragRoleId.value = null
  try {
    const rawData = event.dataTransfer.getData('text/plain')
    if (!rawData) return
    
    let permissionData
    try {
      permissionData = JSON.parse(rawData)
    } catch {
      console.warn('Dropped data is not a valid permission object:', rawData)
      return
    }

    if (!permissionData || !permissionData.id) return

    // Check if permission is already assigned
    const alreadyAssigned = role.permissions && role.permissions.some(p => p.id === permissionData.id)
    if (alreadyAssigned) {
      $q.notify({
        type: 'warning',
        message: `La permission ${permissionData.code} est déjà associée au rôle ${role.name}`
      })
      return
    }

    // Assign on backend
    await assignPermissionToRole(role.id, permissionData.id)
    
    // UI Notification and reload
    $q.notify({
      type: 'positive',
      message: `Permission ${permissionData.code} ajoutée avec succès au rôle ${role.name}`,
      icon: 'check_circle'
    })
    
    // Quick local update to avoid full reload delay
    if (!role.permissions) role.permissions = []
    role.permissions.push(permissionData)
  } catch (err) {
    console.error('Erreur lors du dépôt de la permission', err)
  }
}

// Remove Permission
async function removePermission(role, permission) {
  try {
    await removePermissionFromRole(role.id, permission.id)
    $q.notify({
      type: 'positive',
      message: `Permission ${permission.code} retirée du rôle ${role.name}`
    })
    role.permissions = role.permissions.filter(p => p.id !== permission.id)
  } catch (error) {
    console.error(error)
    $q.notify({
      type: 'negative',
      message: 'Erreur de suppression de la permission'
    })
  }
}

// Gradient mapping for Role Headers
function getRoleHeaderStyle(roleName) {
  if (roleName === 'ADMIN') {
    return 'background: linear-gradient(135deg, #4A00E0 0%, #8E2DE2 100%); border-top-left-radius: 12px; border-top-right-radius: 12px;'
  }
  if (roleName === 'ORGANISATEUR' || roleName === 'USER') {
    return 'background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); border-top-left-radius: 12px; border-top-right-radius: 12px;'
  }
  return 'background: linear-gradient(135deg, #3A7BD5 0%, #3A6073 100%); border-top-left-radius: 12px; border-top-right-radius: 12px;'
}

// Dialog triggers
function openRoleDialog(role = null) {
  $q.dialog({
    component: RoleFormDialog,
    componentProps: { role }
  }).onOk(() => {
    loadData()
  })
}

function openPermissionDialog(permission = null) {
  $q.dialog({
    component: PermissionFormDialog,
    componentProps: { permission }
  }).onOk(() => {
    loadData()
  })
}

// Deletions
function confirmDeleteRole(role) {
  $q.dialog({
    title: t('roleList.deleteRoleTitle'),
    message: `${t('roleList.deleteRoleConfirm')} "${role.name}" ?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await deleteRole(role.id)
      $q.notify({ type: 'positive', message: 'Rôle supprimé avec succès' })
      loadData()
    } catch (error) {
      console.error(error)
      $q.notify({ type: 'negative', message: 'Impossible de supprimer le rôle' })
    }
  })
}

function confirmDeletePermission(permission) {
  $q.dialog({
    title: t('roleList.deletePermissionTitle'),
    message: `${t('roleList.deletePermissionConfirm')} "${permission.code}" ?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await deletePermission(permission.id)
      $q.notify({ type: 'positive', message: 'Permission supprimée avec succès' })
      loadData()
    } catch (error) {
      console.error(error)
      $q.notify({ type: 'negative', message: 'Impossible de supprimer la permission' })
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.perm-item {
  transition: all 0.2s ease;
}
.perm-item:hover {
  background-color: #ECEFF1 !important;
  transform: translateX(4px);
}
.role-card {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(0,0,0,0.05);
  position: relative;
  overflow: hidden;
}
.role-card:hover {
  box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
}
.role-card.drag-over {
  border: 2px dashed #FFB300 !important;
  transform: scale(1.02);
  box-shadow: 0 12px 28px rgba(255, 179, 0, 0.25) !important;
}
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
