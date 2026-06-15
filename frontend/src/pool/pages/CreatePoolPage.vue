<template>
  <q-page class="flex flex-center" style="background: #F5F5F5; padding: 40px 20px;">
    <q-card class="shadow-4" style="width: 100%; max-width: 800px; border-radius: 12px; overflow: hidden;">
      <!-- Header Akkodis Style -->
      <div class="q-pa-lg text-white" style="background: #0D1B2E;">
        <div class="text-h5 text-weight-bold">
          {{ $t('createPool.title') }}
        </div>
        <div class="text-subtitle2" style="color: rgba(255,255,255,0.7);">
          {{ $t('createPool.subtitle') }}
        </div>
      </div>

      <q-form @submit="onSubmit" class="q-pa-xl q-gutter-md">
        <!-- Assistant IA Panel -->
        <q-card class="q-mb-xl q-pa-md text-white bg-gradient-ai shadow-3" style="border-radius: 12px; position: relative; overflow: hidden; background: linear-gradient(135deg, #0D1B2E 0%, #1e3a5f 100%); border-inline-start: 6px solid #FFB300;">
          <div class="row items-center q-col-gutter-sm q-mb-md">
            <div class="col-auto">
              <q-avatar color="amber" text-color="dark" icon="auto_awesome" class="animate-pulse" />
            </div>
            <div class="col">
              <div class="text-subtitle1 text-weight-bold text-amber">{{ $t('createPool.aiTitle') }}</div>
              <div class="text-caption text-grey-3" style="line-height: 1.4;">{{ $t('createPool.aiDesc') }}</div>
            </div>
          </div>
          
          <div class="row q-col-gutter-sm items-start">
            <div class="col-12 col-md-9">
              <q-input
                v-model="aiPrompt"
                type="textarea"
                rows="2"
                dark
                filled
                color="amber"
                :placeholder="$t('createPool.aiPromptPlaceholder')"
                class="q-mb-none"
              />
            </div>
            <div class="col-12 col-md-3 text-center q-pt-sm">
              <q-btn
                :label="$t('createPool.aiButton')"
                icon="bolt"
                color="amber"
                text-color="dark"
                class="full-width q-py-sm text-weight-bold"
                no-caps
                :loading="aiLoading"
                @click="generateWithAi"
              />
            </div>
          </div>

          <!-- Suggestion Preview Panel (shows up when a suggestion is generated) -->
          <div v-if="aiSuggestion" class="q-mt-md q-pa-md bg-white text-dark rounded-borders shadow-1 animate-fade-in" style="border: 1px solid #e0e0e0;">
            <div class="row items-center justify-between q-mb-sm border-bottom q-pb-xs">
              <div class="text-subtitle2 text-weight-bold text-primary">
                <q-icon name="lightbulb" color="warning" class="q-mr-xs" /> {{ $t('createPool.aiSuggestion') }}
              </div>
              <q-badge :label="aiSuggestion.category" color="secondary" />
            </div>

            <div class="q-mb-xs"><strong>{{ $t('createPool.aiSuggestedTitle') }} :</strong> {{ aiSuggestion.title }}</div>
            <div class="q-mb-xs"><strong>{{ $t('createPool.aiSuggestedBudget') }} :</strong> {{ aiSuggestion.goalAmount }} €</div>
            <div class="q-mb-sm text-grey-8" style="font-size: 0.9em; line-height: 1.4;">
              <strong>{{ $t('createPool.aiSuggestedDesc') }} :</strong> {{ aiSuggestion.description }}
            </div>

            <!-- Suggested Structure / Phases preview -->
            <div class="q-mb-md q-pa-sm bg-grey-1 rounded-borders">
              <div class="text-caption text-weight-bold text-grey-7 q-mb-xs">{{ $t('createPool.aiSuggestedStructure') }} :</div>
              <div v-if="aiSuggestion.mode === 'simple'">
                <div class="text-caption text-weight-bold text-secondary q-mb-xs">{{ $t('createPool.aiSuggestedSimple') }} :</div>
                <div v-for="(p, i) in aiSuggestion.simplePhases" :key="i" class="text-caption q-pl-sm">
                  • {{ p.title }} : <strong>{{ p.goalAmount }} €</strong>
                </div>
              </div>
              <div v-else>
                <div class="text-caption text-weight-bold text-secondary q-mb-xs">{{ $t('createPool.aiSuggestedMulti') }} :</div>
                <div v-for="(sp, i) in aiSuggestion.subPools" :key="i" class="text-caption q-pl-sm q-mb-xs">
                  <strong>{{ sp.title }}</strong> :
                  <div v-for="(p, j) in sp.phases" :key="j" class="text-caption q-pl-md" style="font-size: 0.95em;">
                    - {{ p.title }} : {{ p.goalAmount }} €
                  </div>
                </div>
              </div>
            </div>

            <div class="row justify-end q-gutter-sm">
              <q-btn :label="$t('createPool.aiIgnore')" flat color="grey-7" size="md" @click="aiSuggestion = null" no-caps />
              <q-btn :label="$t('createPool.aiApply')" color="primary" class="text-weight-bold" size="md" @click="applyAiSuggestion" no-caps />
            </div>
          </div>
        </q-card>

        <!-- Mode selection -->
        <div class="text-subtitle1 text-weight-bold q-mb-sm" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
          {{ $t('createPool.step1Header') }}
        </div>
        
        <div class="row q-col-gutter-md q-mb-md">
          <div class="col-12 col-sm-6">
            <q-card 
              flat 
              bordered 
              class="cursor-pointer q-pa-md text-center" 
              :style="mode === 'simple' ? 'border: 2px solid #FFB300; background: #FFFBF0;' : ''"
              @click="setMode('simple')"
            >
              <q-icon name="filter_1" size="md" color="primary" class="q-mb-xs" />
              <div class="text-weight-bold">{{ $t('createPool.simplePool') }}</div>
              <div class="text-caption text-grey-7">{{ $t('createPool.simplePoolDesc') }}</div>
            </q-card>
          </div>
          <div class="col-12 col-sm-6">
            <q-card 
              flat 
              bordered 
              class="cursor-pointer q-pa-md text-center" 
              :style="mode === 'multi' ? 'border: 2px solid #FFB300; background: #FFFBF0;' : ''"
              @click="setMode('multi')"
            >
              <q-icon name="account_tree" size="md" color="primary" class="q-mb-xs" />
              <div class="text-weight-bold">{{ $t('createPool.complexPool') }}</div>
              <div class="text-caption text-grey-7">{{ $t('createPool.complexPoolDesc') }}</div>
            </q-card>
          </div>
        </div>

        <!-- Informations de base -->
        <div class="text-subtitle1 text-weight-bold q-mb-sm" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
          {{ $t('createPool.step2Header') }}
        </div>

        <q-input
          outlined
          v-model="form.title"
          :label="$t('createPool.mainTitleLabel')"
          placeholder="Ex: Projet Éco-Solidaire Village"
          lazy-rules
          :rules="[ val => val && val.length > 0 || $t('createPool.titleRequired')]"
          color="secondary"
        />

        <q-input
          outlined
          v-model="form.description"
          type="textarea"
          :label="$t('createPool.descriptionLabel')"
          placeholder="Expliquez votre projet en détails..."
          lazy-rules
          :rules="[ val => val && val.length > 0 || $t('createPool.descriptionRequired')]"
          color="secondary"
        />

        <div class="row q-col-gutter-md">
          <div class="col-12 col-sm-6">
            <q-select
              outlined
              v-model="form.category"
              :options="categoryOptions"
              emit-value
              map-options
              :label="$t('createPool.categoryLabel')"
              color="secondary"
            />
          </div>
          <div class="col-12 col-sm-6">
            <q-input
              outlined
              v-model.number="form.goalAmount"
              type="number"
              :label="$t('createPool.goalAmountLabel')"
              suffix="€"
              lazy-rules
              :rules="[ val => val > 0 || $t('createPool.amountPositive')]"
              color="secondary"
              :readonly="mode === 'multi' || (mode === 'simple' && form.simplePhases.length > 1)"
              :hint="mode === 'multi' || (mode === 'simple' && form.simplePhases.length > 1) ? $t('createPool.calculatedFromPhasesHint') : ''"
              @update:model-value="onTotalGoalUpdate"
            />
          </div>
        </div>

        <!-- Deadline principale -->
        <div class="q-mt-md">
          <div class="row items-center justify-between">
            <div class="text-subtitle2 text-weight-bold text-grey-8">{{ $t('createPool.hasDeadlineToggle') }}</div>
            <q-toggle v-model="form.hasDeadline" color="secondary" />
          </div>
          
          <q-input 
            v-if="form.hasDeadline" 
            outlined 
            v-model="form.deadlineDate" 
            :label="$t('createPool.deadlineLabel')" 
            placeholder="Sélectionnez une date limite"
            :rules="[val => !form.hasDeadline || !!val || $t('createPool.deadlineRequired')]"
          >
            <template v-slot:prepend>
              <q-icon name="event" class="cursor-pointer">
                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                  <q-date v-model="form.deadlineDate" mask="YYYY-MM-DDTHH:mm:ss">
                    <div class="row items-center justify-end">
                      <q-btn v-close-popup :label="$t('createPool.cancel')" color="primary" flat />
                    </div>
                  </q-date>
                </q-popup-proxy>
              </q-icon>
            </template>
            <template v-slot:append>
              <q-icon name="access_time" class="cursor-pointer">
                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                  <q-time v-model="form.deadlineDate" mask="YYYY-MM-DDTHH:mm:ss" format24h>
                    <div class="row items-center justify-end">
                      <q-btn v-close-popup :label="$t('createPool.cancel')" color="primary" flat />
                    </div>
                  </q-time>
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
        </div>

        <!-- SOUS-CAGNOTTES SECTION (Only if mode === 'multi') -->
        <div v-if="mode === 'multi'" class="q-mt-lg">
          <div class="row items-center justify-between q-mb-md">
            <div class="text-subtitle1 text-weight-bold" :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#0D1B2E', borderBottom: '2px solid #FFB300' }" style="display: inline-block;">
              {{ $t('createPool.step3ComplexHeader') }}
            </div>
            <q-btn 
              :label="$t('createPool.addSubPoolButton')" 
              icon="add" 
              color="primary" 
              outline 
              no-caps 
              @click="addSubPool" 
            />
          </div>

          <div v-for="(subPool, spIndex) in form.subPools" :key="spIndex" class="q-pa-md q-mb-md bg-grey-2 rounded-borders border-accent relative-position" :style="{ borderInlineStart: $q.dark.isActive ? '6px solid #FFA726' : '6px solid #0D1B2E' }">
            <q-btn 
              v-if="form.subPools.length > 1"
              icon="close" 
              flat 
              round 
              dense 
              color="negative" 
              class="absolute-top-right q-ma-sm"
              @click="removeSubPool(spIndex)" 
            />
            
            <div class="text-subtitle2 text-weight-bold q-mb-md">{{ $t('createPool.subPoolTitleDefault') }} {{ spIndex + 1 }}</div>

            <div class="q-gutter-y-sm">
              <q-input 
                outlined 
                v-model="subPool.title" 
                dense 
                :label="$t('createPool.subPoolTitleLabel')" 
                placeholder="Ex: Phase 1: Fondation" 
                bg-white
                :rules="[val => !!val || $t('createPool.titleRequired')]"
              />

              <q-input 
                outlined 
                v-model="subPool.description" 
                dense 
                type="textarea"
                rows="2"
                :label="$t('createPool.subPoolDescLabel')" 
                placeholder="Détails de cette sous-cagnotte..." 
                bg-white
              />

              <div class="row items-center justify-between">
                <span class="text-caption text-grey-8">{{ $t('createPool.subPoolDeadlineToggle') }}</span>
                <q-toggle v-model="subPool.hasDeadline" dense color="secondary" />
              </div>

              <q-input 
                v-if="subPool.hasDeadline" 
                outlined 
                dense 
                v-model="subPool.deadlineDate" 
                :label="$t('createPool.deadlineLabel')" 
                bg-white
                :rules="[val => !subPool.hasDeadline || !!val || $t('createPool.deadlineRequired')]"
              >
                <template v-slot:prepend>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                      <q-date v-model="subPool.deadlineDate" mask="YYYY-MM-DDTHH:mm:ss">
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup :label="$t('createPool.cancel')" color="primary" flat />
                        </div>
                      </q-date>
                    </q-popup-proxy>
                  </q-icon>
                </template>
                <template v-slot:append>
                  <q-icon name="access_time" class="cursor-pointer">
                    <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                      <q-time v-model="subPool.deadlineDate" mask="YYYY-MM-DDTHH:mm:ss" format24h>
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup :label="$t('createPool.cancel')" color="primary" flat />
                        </div>
                      </q-time>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>

              <!-- PHASES for this subPool -->
              <div class="q-mt-md">
                <div class="row items-center justify-between q-mb-sm">
                  <span class="text-weight-bold text-caption">{{ $t('createPool.subPoolPhasesHeader') }}</span>
                  <q-btn 
                    :label="$t('createPool.addSubPoolPhaseButton')" 
                    icon="add" 
                    dense 
                    flat 
                    color="secondary" 
                    no-caps 
                    @click="addPhaseToSubPool(spIndex)" 
                  />
                </div>

                <div v-for="(phase, phIndex) in subPool.phases" :key="phIndex" class="row q-col-gutter-sm items-center q-mb-xs">
                  <div class="col-6">
                    <q-input 
                      outlined 
                      v-model="phase.title" 
                      dense 
                      :label="$t('createPool.phaseTitleLabel')" 
                      placeholder="Ex: Étape 1" 
                      bg-white
                      :rules="[val => !!val || $t('createPool.titleRequired')]"
                    />
                  </div>
                  <div class="col-4">
                    <q-input 
                      outlined 
                      v-model.number="phase.goalAmount" 
                      dense 
                      type="number"
                      :label="$t('createPool.phaseGoalLabel')" 
                      suffix="€" 
                      bg-white
                      :rules="[val => val > 0 || $t('createPool.amountPositive')]"
                      @update:model-value="calculateTotalGoal"
                    />
                  </div>
                  <div class="col-2 text-right">
                    <q-btn 
                      v-if="subPool.phases.length > 1"
                      icon="delete" 
                      flat 
                      round 
                      dense 
                      color="negative" 
                      @click="removePhaseFromSubPool(spIndex, phIndex)" 
                    />
                  </div>
                </div>
              </div>

              <div class="text-right text-caption text-weight-bold text-primary q-mt-xs">
                {{ $t('createPool.subPoolGoalLabel') }} {{ getSubPoolGoal(subPool) }} €
              </div>
            </div>
          </div>
        </div>

        <!-- PHASES SECTION (Only if mode === 'simple') -->
        <div v-if="mode === 'simple'" class="q-mt-lg">
          <div class="row items-center justify-between q-mb-md">
            <div class="text-subtitle1 text-weight-bold" :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#0D1B2E', borderBottom: '2px solid #FFB300' }" style="display: inline-block;">
              {{ $t('createPool.phasesHeaderSimple') }}
            </div>
            <q-btn 
              :label="$t('createPool.addPhaseButton')" 
              icon="add" 
              color="primary" 
              outline 
              no-caps 
              @click="addSimplePhase" 
            />
          </div>

          <div v-for="(phase, phIndex) in form.simplePhases" :key="phIndex" class="row q-col-gutter-sm items-center q-mb-sm">
            <div class="col-6">
              <q-input 
                outlined 
                v-model="phase.title" 
                dense 
                :label="$t('createPool.phaseTitleLabel')" 
                placeholder="Ex: Achat de matériel" 
                bg-white
                :rules="[val => !!val || $t('createPool.titleRequired')]"
              />
            </div>
            <div class="col-4">
              <q-input 
                outlined 
                v-model.number="phase.goalAmount" 
                dense 
                type="number"
                :label="$t('createPool.phaseGoalLabel')" 
                suffix="€" 
                bg-white
                :rules="[val => val > 0 || $t('createPool.amountPositive')]"
                @update:model-value="calculateTotalGoal"
              />
            </div>
            <div class="col-2 text-right">
              <q-btn 
                v-if="form.simplePhases.length > 1"
                icon="delete" 
                flat 
                round 
                dense 
                color="negative" 
                @click="removeSimplePhase(phIndex)" 
              />
            </div>
          </div>
        </div>

        <!-- Upload d'image -->
        <div class="text-subtitle1 text-weight-bold q-mt-lg q-mb-sm" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
          {{ $t('createPool.mediaHeader') }}
        </div>

        <div class="q-mb-md">
          <q-file
            outlined
            v-model="imageFile"
            :label="$t('createPool.imageLabel')"
            accept=".jpg, .jpeg, .png, .webp"
            @update:model-value="onFileSelected"
            color="secondary"
          >
            <template v-slot:prepend>
              <q-icon name="cloud_upload" />
            </template>
          </q-file>
          
          <div v-if="imagePreview" class="q-mt-md row justify-center">
            <q-img
              :src="imagePreview"
              style="max-width: 100%; height: 200px; border-radius: 12px; border: 2px solid #FFB300;"
              fit="cover"
            />
          </div>
        </div>
        
        <!-- Vidéo (Optionnel) -->
        <div class="q-mb-md">
          <div class="text-subtitle2 q-mb-sm text-grey-7">{{ $t('createPool.videoUrlLabel') }}</div>
          
          <q-tabs
            v-model="videoSource"
            dense
            class="text-grey"
            active-color="primary"
            indicator-color="primary"
            align="left"
            narrow-indicator
            no-caps
          >
            <q-tab name="url" :label="$t('createPool.videoTabUrl')" />
            <q-tab name="upload" :label="$t('createPool.videoTabUpload')" />
          </q-tabs>

          <q-tab-panels v-model="videoSource" animated style="background: transparent;">
            <q-tab-panel name="url" class="q-pa-none q-pt-md">
              <q-input
                outlined
                v-model="form.videoUrl"
                :label="$t('createPool.videoUrlLabel')"
                placeholder="https://www.youtube.com/watch?v=..."
                color="secondary"
                :hint="$t('createPool.videoUrlLabel')"
              >
                <template v-slot:prepend>
                  <q-icon name="link" />
                </template>
              </q-input>
            </q-tab-panel>

            <q-tab-panel name="upload" class="q-pa-none q-pt-md">
              <q-file
                outlined
                v-model="videoFile"
                :label="$t('createPool.videoTabUpload')"
                accept="video/*"
                color="secondary"
                :hint="$t('createPool.uploadingVideo')"
                @update:model-value="onVideoSelected"
              >
                <template v-slot:prepend>
                  <q-icon name="movie_filter" />
                </template>
              </q-file>
              
              <div v-if="videoFilePreview" class="q-mt-md">
                <video controls style="width: 100%; border-radius: 12px; border: 2px solid #FFB300;">
                  <source :src="videoFilePreview" type="video/mp4">
                  {{ $t('createPool.videoUploadError') }}
                </video>
              </div>
            </q-tab-panel>
          </q-tab-panels>
          
          <div v-if="videoPreviewId && videoSource === 'url'" class="q-mt-md">
            <q-video
              :ratio="16/9"
              :src="`https://www.youtube.com/embed/${videoPreviewId}`"
              style="border-radius: 12px; border: 2px solid #FFB300;"
            />
          </div>
        </div>

        <!-- Paramètres de visibilité -->
        <div class="text-subtitle1 text-weight-bold q-mt-lg q-mb-sm" style="color: #0D1B2E; border-bottom: 2px solid #FFB300; display: inline-block;">
          {{ $t('createPool.typeTitle') }}
        </div>

        <div class="row q-gutter-md">
          <q-radio v-model="form.type" val="PUBLIC" :label="$t('createPool.typePublicRadio')" color="primary" />
          <q-radio v-model="form.type" val="PRIVATE" :label="$t('createPool.typePrivateRadio')" color="primary" />
          <q-radio v-model="form.type" val="PRIVATE_TONTINE" :label="$t('createPool.typeTontineRadio')" color="primary" />
        </div>

        <p class="text-caption text-grey-7">
          * {{ $t('createPool.typePublicDesc') }}
        </p>

        <!-- Actions -->
        <div class="row justify-end q-mt-xl q-gutter-sm">
          <q-btn icon="bolt" flat color="amber-9" @click="autoFill" :label="$t('createPool.autoFillBtn')" no-caps />
          <q-btn :label="$t('createPool.cancel')" flat color="grey-7" v-close-popup @click="$router.back()" no-caps />
          <q-btn
            :label="$t('createPool.submitBtn')"
            type="submit"
            style="background: #FFB300; color: #1A1A2A; font-weight: 700; padding: 10px 24px;"
            no-caps
            :loading="loading"
          />
        </div>
      </q-form>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { poolApi } from 'boot/axios'
import { useQuasar } from 'quasar'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import authStore from '../../shared/stores/auth'

const $q = useQuasar()
const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const loading = ref(false)
const imageFile = ref(null)

const aiPrompt = ref('')
const aiLoading = ref(false)
const aiSuggestion = ref(null)

const generateWithAi = async () => {
  if (!aiPrompt.value.trim()) {
    $q.notify({
      type: 'warning',
      message: t('createPool.aiPromptWarning')
    })
    return
  }
  
  aiLoading.value = true
  aiSuggestion.value = null
  
  try {
    const response = await poolApi.post('/pools/ai/generate', {
      prompt: aiPrompt.value,
      mode: mode.value
    })
    aiSuggestion.value = response.data
    $q.notify({
      type: 'positive',
      message: t('createPool.aiSuccessMsg'),
      position: 'top-right'
    })
  } catch (error) {
    console.error(error)
    $q.notify({
      type: 'negative',
      message: t('createPool.createError') + (error.response?.data?.message || error.response?.data?.error || 'Serveur injoignable'),
      position: 'top-right'
    })
  } finally {
    aiLoading.value = false
  }
}

const applyAiSuggestion = () => {
  if (!aiSuggestion.value) return
  
  const sugg = aiSuggestion.value
  
  form.title = sugg.title || ''
  form.description = sugg.description || ''
  
  if (categories.includes(sugg.category)) {
    form.category = sugg.category
  } else {
    form.category = 'Projets'
  }
  
  mode.value = sugg.mode || 'simple'
  
  if (mode.value === 'simple') {
    if (sugg.simplePhases && sugg.simplePhases.length > 0) {
      form.simplePhases = sugg.simplePhases.map(p => ({
        title: p.title,
        goalAmount: Number(p.goalAmount) || 0
      }))
    } else {
      form.simplePhases = [{ title: t('createPool.phaseUniqueTitle'), goalAmount: Number(sugg.goalAmount) || 1000 }]
    }
  } else {
    if (sugg.subPools && sugg.subPools.length > 0) {
      form.subPools = sugg.subPools.map(sp => ({
        title: sp.title || t('createPool.subPoolTitleDefault'),
        description: sp.description || '',
        hasDeadline: !!sp.hasDeadline,
        deadlineDate: '',
        phases: sp.phases && sp.phases.length > 0 ? sp.phases.map(p => ({
          title: p.title,
          goalAmount: Number(p.goalAmount) || 0
        })) : [{ title: t('createPool.stepDefault1Title'), goalAmount: 500 }]
      }))
    } else {
      form.subPools = [{
        title: t('createPool.subPoolDefault1Title'),
        description: t('createPool.subPoolDefault1Desc'),
        hasDeadline: false,
        deadlineDate: '',
        phases: [{ title: t('createPool.stepDefault1Title'), goalAmount: Number(sugg.goalAmount) || 1000 }]
      }]
    }
  }
  
  calculateTotalGoal()
  
  $q.notify({
    icon: 'auto_awesome',
    message: t('createPool.aiAppliedMsg'),
    color: 'primary',
    position: 'top-right'
  })
}
const imagePreview = ref(null)
const videoFile = ref(null)
const videoFilePreview = ref(null)
const videoSource = ref('url')

const mode = ref('simple')

const setMode = (newMode) => {
  mode.value = newMode
  calculateTotalGoal()
}

const onVideoSelected = (file) => {
  if (file) {
    videoFilePreview.value = URL.createObjectURL(file)
  } else {
    videoFilePreview.value = null
  }
}

const categories = ['Santé', 'Éducation', 'Urgence', 'Animaux', 'Projets', 'Sport']
const getCategoryLabel = (catName) => {
  const keyMap = {
    'Santé': 'catHealth',
    'Éducation': 'catEducation',
    'Urgence': 'catEmergency',
    'Animaux': 'catAnimals',
    'Projets': 'catProjects',
    'Sport': 'catSport'
  }
  const key = keyMap[catName]
  return key ? t(`home.${key}`) : catName
}
const categoryOptions = computed(() => categories.map(cat => ({
  value: cat,
  label: getCategoryLabel(cat)
})))

const form = reactive({
  title: '',
  description: '',
  category: 'Santé',
  goalAmount: 1000,
  type: 'PUBLIC',
  ownerId: '',
  videoUrl: '',
  parentId: route.query.parentId || null,
  hasDeadline: false,
  deadlineDate: '',
  simplePhases: [
    { title: t('createPool.phaseUniqueTitle'), goalAmount: 1000 }
  ],
  subPools: [
    {
      title: t('createPool.subPoolDefault1Title'),
      description: t('createPool.subPoolDefault1Desc'),
      hasDeadline: false,
      deadlineDate: '',
      phases: [
        { title: t('createPool.stepDefault1Title'), goalAmount: 500 }
      ]
    }
  ]
})

const addSubPool = () => {
  form.subPools.push({
    title: `${t('createPool.subPoolTitleDefault')} ${form.subPools.length + 1}`,
    description: '',
    hasDeadline: false,
    deadlineDate: '',
    phases: [
      { title: t('createPool.stepDefault1Title'), goalAmount: 500 }
    ]
  })
  calculateTotalGoal()
}

const removeSubPool = (index) => {
  if (form.subPools.length > 1) {
    form.subPools.splice(index, 1)
    calculateTotalGoal()
  }
}

const addPhaseToSubPool = (spIndex) => {
  const stepWord = t('createPool.stepDefault1Title').replace('1', '').trim()
  form.subPools[spIndex].phases.push({
    title: `${stepWord} ${form.subPools[spIndex].phases.length + 1}`,
    goalAmount: 500
  })
  calculateTotalGoal()
}

const removePhaseFromSubPool = (spIndex, phIndex) => {
  if (form.subPools[spIndex].phases.length > 1) {
    form.subPools[spIndex].phases.splice(phIndex, 1)
    calculateTotalGoal()
  }
}

const addSimplePhase = () => {
  const stepWord = t('createPool.stepDefault1Title').replace('1', '').trim()
  if (form.simplePhases.length === 1 && form.simplePhases[0].title === t('createPool.phaseUniqueTitle')) {
    form.simplePhases[0].title = `${stepWord} 1`
  }
  form.simplePhases.push({
    title: `${stepWord} ${form.simplePhases.length + 1}`,
    goalAmount: 500
  })
  calculateTotalGoal()
}

const removeSimplePhase = (index) => {
  if (form.simplePhases.length > 1) {
    form.simplePhases.splice(index, 1)
    if (form.simplePhases.length === 1) {
      form.simplePhases[0].title = t('createPool.phaseUniqueTitle')
    }
    calculateTotalGoal()
  }
}

const onTotalGoalUpdate = (val) => {
  if (mode.value === 'simple' && form.simplePhases.length === 1) {
    form.simplePhases[0].goalAmount = Number(val) || 0
  }
}

const getSubPoolGoal = (subPool) => {
  return subPool.phases.reduce((sum, phase) => sum + (Number(phase.goalAmount) || 0), 0)
}

const calculateTotalGoal = () => {
  if (mode.value === 'multi') {
    form.goalAmount = form.subPools.reduce((sum, subPool) => sum + getSubPoolGoal(subPool), 0)
  } else if (mode.value === 'simple') {
    form.goalAmount = form.simplePhases.reduce((sum, phase) => sum + (Number(phase.goalAmount) || 0), 0)
  }
}

const videoPreviewId = computed(() => {
  if (!form.videoUrl) return null
  const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/
  const match = form.videoUrl.match(regExp)
  return (match && match[2].length === 11) ? match[2] : null
})

const onFileSelected = (file) => {
  if (file) {
    imagePreview.value = URL.createObjectURL(file)
  } else {
    imagePreview.value = null
  }
}

const autoFill = () => {
  form.title = 'Construction d\'une École Primaire'
  form.description = 'Ce projet vise à construire une école pour les enfants du village. Nous avons divisé le projet en étapes clés pour assurer un suivi transparent.'
  form.category = 'Éducation'
  mode.value = 'multi'
  form.hasDeadline = true
  form.deadlineDate = '2026-12-31T18:00:00'
  form.subPools = [
    {
      title: 'Infrastructure de base',
      description: 'Fondations et murs du bâtiment principal',
      hasDeadline: true,
      deadlineDate: '2026-06-30T18:00:00',
      phases: [
        { title: 'Terrassement', goalAmount: 2000 },
        { title: 'Dalle de béton', goalAmount: 3000 }
      ]
    },
    {
      title: 'Finition & Toiture',
      description: 'Mise hors d\'eau hors d\'air',
      hasDeadline: false,
      deadlineDate: '',
      phases: [
        { title: 'Charpente', goalAmount: 4000 },
        { title: 'Second œuvre', goalAmount: 2000 }
      ]
    }
  ]
  calculateTotalGoal()
  form.type = 'PUBLIC'
  
  $q.notify({
    icon: 'bolt',
    message: t('createPool.simulatedProject'),
    color: 'amber-9',
    position: 'top-right'
  })
}

const onSubmit = async () => {
  loading.value = true
  try {
    const userId = authStore.user.value?.id
    
    if (!userId) {
      $q.notify({ type: 'negative', message: t('createPool.mustBeLoggedIn') })
      loading.value = false
      return
    }

    let imageData = null
    let imageContentType = null

    if (imageFile.value) {
      imageData = await new Promise((resolve) => {
        const reader = new FileReader()
        reader.onloadend = () => resolve(reader.result)
        reader.readAsDataURL(imageFile.value)
      })
      imageContentType = imageFile.value.type
    }

    let finalVideoUrl = form.videoUrl

    // Build the payload
    const mainPoolPayload = {
      title: form.title,
      description: form.description,
      category: form.category,
      goalAmount: form.goalAmount,
      type: form.type,
      ownerId: userId,
      imageData: imageData,
      imageContentType: imageContentType,
      videoUrl: finalVideoUrl,
      parentId: form.parentId,
      hasDeadline: form.hasDeadline,
      deadlineDate: form.hasDeadline ? form.deadlineDate : null,
      phases: [],
      subPools: []
    }

    if (mode.value === 'simple') {
      mainPoolPayload.phases = form.simplePhases.map(p => ({
        title: p.title,
        goalAmount: p.goalAmount
      }))
    } else {
      mainPoolPayload.subPools = form.subPools.map(sp => ({
        title: sp.title,
        description: sp.description,
        hasDeadline: sp.hasDeadline,
        deadlineDate: sp.hasDeadline ? sp.deadlineDate : null,
        phases: sp.phases.map(p => ({
          title: p.title,
          goalAmount: p.goalAmount
        }))
      }))
    }

    const response = await poolApi.post('/pools', mainPoolPayload)
    const mainPoolId = response.data.id

    // Upload Video if selected
    if (videoSource.value === 'upload' && videoFile.value) {
      $q.notify({
        message: t('createPool.uploadingVideo'),
        color: 'primary',
        icon: 'movie'
      })
      try {
        const formData = new FormData()
        formData.append('file', videoFile.value)
        
        await poolApi.post(`/pools/${mainPoolId}/video`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
      } catch (err) {
        console.error('Erreur upload vidéo:', err)
        $q.notify({
          type: 'negative',
          message: t('createPool.videoUploadError')
        })
      }
    }
    
    $q.notify({
      type: 'positive',
      message: t('createPool.success'),
      position: 'top'
    })
    
    router.push(`/pools/${mainPoolId}`)
  } catch (error) {
    console.error(error)
    $q.notify({
      type: 'negative',
      message: t('createPool.createError') + (error.response?.data?.message || 'Serveur injoignable'),
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.q-card {
  transition: all 0.3s ease;
}
.phase-config {
  transition: all 0.3s ease;
}
</style>
