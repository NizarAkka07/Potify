<template>
  <q-page class="flex flex-center" style="background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%); min-height: 100vh; padding: 40px 20px;">
    <q-card class="payment-card q-pa-xl text-center text-white relative-position overflow-hidden" style="max-width: 500px; width: 100%; border-radius: 24px; border: 1px solid rgba(255,255,255,0.1); background: rgba(30, 41, 59, 0.7); backdrop-filter: blur(16px); box-shadow: 0 20px 50px rgba(0,0,0,0.3);">
      
      <!-- GLOW EFFECT -->
      <div class="absolute-top-right" style="width: 200px; height: 200px; background: radial-gradient(circle, rgba(99,102,241,0.15) 0%, rgba(99,102,241,0) 70%); top: -50px; right: -50px;"></div>
      <div class="absolute-bottom-left" style="width: 200px; height: 200px; background: radial-gradient(circle, rgba(16,185,129,0.1) 0%, rgba(16,185,129,0) 70%); bottom: -50px; left: -50px;"></div>

      <!-- STATE 1: LOADING / PROCESSING -->
      <div v-if="state === 'loading'" class="column items-center justify-center q-gutter-y-lg q-py-lg">
        <q-spinner-oval color="primary" size="80px" thickness="3" />
        <div>
          <h2 class="text-h5 text-weight-bold q-mt-none q-mb-xs">{{ $t('paymentSuccess.loadingTitle') }}</h2>
          <p class="text-subtitle2 text-grey-4">{{ $t('paymentSuccess.loadingDesc') }}</p>
        </div>
      </div>

      <!-- STATE 2: SUCCESS -->
      <div v-else-if="state === 'success'" class="column items-center justify-center q-gutter-y-md">
        <!-- Success Icon with Pulsing Effect -->
        <div class="icon-container bg-emerald-5 flex flex-center q-mb-md">
          <q-icon name="check" color="emerald" size="56px" class="success-check" />
        </div>
        
        <div>
          <h2 class="text-h4 text-weight-bolder text-emerald-4 q-mt-none q-mb-sm">{{ $t('paymentSuccess.successTitle') }}</h2>
          <p class="text-subtitle1 text-grey-3 q-px-md">
            {{ $t('paymentSuccess.successDesc') }}
          </p>
        </div>

        <q-separator dark class="full-width q-my-md" style="background: rgba(255,255,255,0.08);" />

        <!-- Info Grid -->
        <div class="full-width q-gutter-y-sm text-left q-px-md">
          <div class="row justify-between text-body2">
            <span class="text-grey-4">{{ $t('paymentSuccess.paymentMethod') }} :</span>
            <span class="text-weight-bold text-white text-uppercase">{{ method }}</span>
          </div>
          <div class="row justify-between text-body2">
            <span class="text-grey-4">{{ $t('paymentSuccess.transactionId') }} :</span>
            <span class="text-weight-bold text-indigo-3 text-caption font-mono line-clamp-1" style="max-width: 220px;">
              {{ transactionId }}
            </span>
          </div>
        </div>

        <q-separator dark class="full-width q-my-md" style="background: rgba(255,255,255,0.08);" />

        <div class="full-width q-mt-lg">
          <q-btn
            :label="$t('paymentSuccess.backToPoolButton')"
            color="emerald"
            class="full-width q-py-md text-weight-bold return-btn"
            unelevated
            no-caps
            style="border-radius: 12px; font-size: 1.05rem;"
            @click="goBack"
          />
          <p class="text-caption text-grey-5 q-mt-md">
            {{ $t('paymentSuccess.redirectTimer') }} {{ countdown }} {{ $t('paymentSuccess.seconds') }}...
          </p>
        </div>
      </div>

      <!-- STATE 3: ERROR -->
      <div v-else class="column items-center justify-center q-gutter-y-md">
        <div class="icon-container bg-rose-5 flex flex-center q-mb-md">
          <q-icon name="error_outline" color="rose" size="56px" />
        </div>

        <div>
          <h2 class="text-h4 text-weight-bolder text-rose-4 q-mt-none q-mb-sm">{{ $t('paymentSuccess.errorTitle') }}</h2>
          <p class="text-subtitle1 text-grey-3 q-px-md">
            Nous n'avons pas pu valider votre paiement. Veuillez vérifier vos informations ou réessayer ultérieurement.
          </p>
        </div>

        <q-separator dark class="full-width q-my-md" style="background: rgba(255,255,255,0.08);" />

        <div class="full-width q-mt-lg">
          <q-btn
            :label="$t('paymentSuccess.retryButton')"
            color="rose"
            class="full-width q-py-md text-weight-bold"
            unelevated
            no-caps
            style="border-radius: 12px; font-size: 1.05rem;"
            @click="goBack"
          />
        </div>
      </div>

    </q-card>
  </q-page>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { poolService } from 'src/shared/services/poolService'
import { useQuasar } from 'quasar'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const state = ref('loading') // 'loading', 'success', 'error'
const method = ref('')
const transactionId = ref('')
const countdown = ref(7)
let timer = null

const goBack = () => {
  router.push('/pools')
}

onMounted(async () => {
  const queryMethod = route.query.method
  method.value = queryMethod || 'Paiement'
  
  try {
    if (queryMethod === 'stripe') {
      const sessionId = route.query.session_id
      if (!sessionId) throw new Error('ID de session Stripe manquant')
      transactionId.value = sessionId
      
      // Valider le paiement Stripe auprès de notre microservice
      await poolService.confirmStripePayment(sessionId)
      state.value = 'success'
    } else if (queryMethod === 'paypal') {
      const orderId = route.query.token
      if (!orderId) throw new Error('ID de commande PayPal manquant')
      transactionId.value = orderId
      
      // Valider le paiement PayPal auprès de notre microservice
      await poolService.confirmPayPalPayment(orderId)
      state.value = 'success'
    } else {
      throw new Error('Méthode de paiement non prise en charge')
    }

    // Lancer le compte à rebours de redirection
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        goBack()
      }
    }, 1000)

  } catch (err) {
    console.error('Erreur lors de la confirmation du paiement:', err)
    state.value = 'error'
    $q.notify({
      type: 'negative',
      message: err.response?.data?.message || t('paymentSuccess.errorDesc')
    })
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.payment-card {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.icon-container {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  position: relative;
}

.bg-emerald-5 {
  background: rgba(16, 185, 129, 0.15);
  border: 2px solid rgba(16, 185, 129, 0.3);
}

.text-emerald-4 {
  color: #34d399;
}

.bg-rose-5 {
  background: rgba(244, 63, 94, 0.15);
  border: 2px solid rgba(244, 63, 94, 0.3);
}

.text-rose-4 {
  color: #fb7185;
}

.font-mono {
  font-family: 'Courier New', Courier, monospace;
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Animations micro-interactives */
.success-check {
  animation: scaleIn 0.5s ease-out forwards;
}

@keyframes scaleIn {
  0% {
    transform: scale(0.5);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.return-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
  transition: transform 0.2s, filter 0.2s;
}

.return-btn:hover {
  transform: translateY(-2px);
  filter: brightness(1.1);
}
</style>
