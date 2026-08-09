<template>
  <q-page class="q-pb-xl" :class="$q.dark.isActive ? 'bg-dark text-white' : 'bg-grey-1 text-dark'">
    <!-- Hero Header Section -->
    <div class="faq-hero q-py-xl q-px-md text-center text-white" style="background: linear-gradient(135deg, #054f31 0%, #0d7048 50%, #16a34a 100%);">
      <div style="max-width: 800px; margin: 0 auto;">
        <q-avatar size="64px" class="q-mb-md bg-white text-positive shadow-6">
          <q-icon name="help_outline" size="36px" />
        </q-avatar>
        <h1 class="text-h3 text-weight-bolder q-mb-sm tracking-tight font-serif" style="font-family: 'Playfair Display', serif;">
          Aide et questions fréquentes
        </h1>
        <p class="text-subtitle1 text-green-1 q-mb-lg opacity-90" style="max-width: 650px; margin-left: auto; margin-right: auto; line-height: 1.6;">
          L'équipe Potify répond à vos questions les plus fréquentes pour vous aider à lancer, gérer ou participer à une cagnotte sereinement.
        </p>

        <!-- Search Bar -->
        <div class="search-box-wrapper q-mx-auto" style="max-width: 580px;">
          <q-input
            v-model="searchQuery"
            outlined
            rounded
            bg-color="white"
            placeholder="Recherche par mots-clés (ex: création, virement, frais...)"
            class="shadow-10 text-body1"
            clearable
          >
            <template #prepend>
              <q-icon name="search" color="positive" size="24px" />
            </template>
          </q-input>
        </div>
      </div>
    </div>

    <!-- Quick Action / Category Filters -->
    <div class="container-center q-px-md q-pt-lg" style="max-width: 900px; margin: 0 auto;">
      <div class="row items-center justify-center q-gutter-sm q-mb-lg">
        <q-btn
          v-for="cat in categories"
          :key="cat.id"
          rounded
          unelevated
          no-caps
          size="md"
          :color="selectedCategory === cat.id ? 'positive' : ($q.dark.isActive ? 'grey-9' : 'white')"
          :text-color="selectedCategory === cat.id ? 'white' : ($q.dark.isActive ? 'grey-3' : 'grey-9')"
          :class="['category-pill', selectedCategory === cat.id ? 'shadow-3' : 'border-grey']"
          @click="selectedCategory = cat.id"
        >
          <q-icon :name="cat.icon" class="q-mr-xs" size="18px" />
          <span class="text-weight-bold">{{ cat.label }}</span>
        </q-btn>
      </div>

      <!-- FAQ Accordion List -->
      <div class="faq-accordion-section q-mb-xl">
        <div v-if="filteredFaqs.length === 0" class="text-center q-pa-xl bg-white rounded-borders shadow-1">
          <q-icon name="search_off" size="48px" color="grey-5" class="q-mb-md" />
          <div class="text-h6 text-grey-8 text-weight-bold">Aucune question ne correspond à votre recherche</div>
          <div class="text-caption text-grey-6 q-mb-md">Essayez avec d'autres mots-clés ou consultez notre équipe support.</div>
          <q-btn color="positive" label="Réinitialiser la recherche" outline @click="resetFilters" />
        </div>

        <q-list v-else class="q-gutter-y-md">
          <q-card
            v-for="faq in filteredFaqs"
            :key="faq.id"
            flat
            bordered
            class="faq-card rounded-borders overflow-hidden transition-all"
            :class="$q.dark.isActive ? 'bg-dark-light border-grey-dark' : 'bg-white border-grey-light'"
          >
            <q-expansion-item
              group="faq-group"
              expand-separator
              header-class="q-py-md text-weight-bold text-subtitle1"
              :duration="250"
            >
              <template #header>
                <q-item-section avatar>
                  <q-avatar size="38px" :class="$q.dark.isActive ? 'bg-grey-9 text-green-4' : 'bg-green-1 text-positive'">
                    <q-icon :name="faq.icon" size="20px" />
                  </q-avatar>
                </q-item-section>

                <q-item-section>
                  <div class="text-weight-bold text-subtitle1" :class="$q.dark.isActive ? 'text-white' : 'text-grey-9'">
                    {{ faq.question }}
                  </div>
                  <div class="text-caption text-grey-6">
                    {{ getCategoryLabel(faq.category) }}
                  </div>
                </q-item-section>
              </template>

              <q-card-section class="q-pt-none q-pb-md q-px-lg text-body1" :class="$q.dark.isActive ? 'text-grey-3' : 'text-grey-8'" style="line-height: 1.7; white-space: pre-line;">
                <q-separator class="q-mb-md" />
                {{ faq.answer }}
              </q-card-section>
            </q-expansion-item>
          </q-card>
        </q-list>
      </div>

      <!-- Human Assistance Request Banner (Contact Support Direct) -->
      <q-card v-if="isAnyAdminOnline" flat bordered class="human-support-card rounded-borders overflow-hidden shadow-6 text-white" style="background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%); border: 1px solid rgba(255,255,255,0.1);">
        <q-card-section class="q-pa-lg">
          <div class="row items-center justify-between q-col-gutter-md">
            <div class="col-12 col-md-8">
              <div class="row items-center q-gutter-x-sm q-mb-xs">
                <q-icon name="support_agent" color="green-4" size="32px" />
                <span class="text-h5 text-weight-bolder">Besoin d'une assistance humaine ?</span>
              </div>
              <p class="text-grey-4 text-body1 q-mb-none" style="line-height: 1.5;">
                Vous ne trouvez pas la réponse exacte à votre problème ? Notre équipe de conseillers et administrateurs Potify est disponible en direct pour vous accompagner.
              </p>
            </div>
            
            <div class="col-12 col-md-4 text-right flex flex-center justify-md-end">
              <q-btn
                unelevated
                color="positive"
                icon="chat_bubble"
                label="Démarrer le Chat Admin"
                size="lg"
                class="text-weight-bolder shadow-4 rounded-borders full-width-sm"
                @click="openHumanChat"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useQuasar } from 'quasar'
import { checkAdminAvailability } from 'src/shared/services/supportService'

const $q = useQuasar()
const searchQuery = ref('')
const selectedCategory = ref('all')
const isAnyAdminOnline = ref(false)
let availabilityTimer = null

const fetchAvailability = async () => {
  try {
    const res = await checkAdminAvailability()
    isAnyAdminOnline.value = !!res.data?.online
  } catch {
    isAnyAdminOnline.value = false
  }
}

onMounted(() => {
  fetchAvailability()
  availabilityTimer = setInterval(fetchAvailability, 5000)
})

onUnmounted(() => {
  if (availabilityTimer) {
    clearInterval(availabilityTimer)
  }
})

const categories = [
  { id: 'creation', label: 'Créer une cagnotte', icon: 'add_circle_outline' },
  { id: 'payment', label: 'Paiements & Virement', icon: 'account_balance_wallet' },
  { id: 'security', label: 'Sécurité & Frais', icon: 'shield' },
  { id: 'account', label: 'Compte & Profil', icon: 'person' }
]

const faqs = [
  {
    id: 1,
    category: 'creation',
    icon: 'help_outline',
    question: 'Qui peut lancer une cagnotte et pour quoi ?',
    answer: `Tout utilisateur inscrit sur Potify peut créer une cagnotte en quelques clics. Les cagnottes peuvent être créées pour une multitude d'occasions :
• Événements personnels : Anniversaire, mariage, pot de départ, naissance, voyage.
• Projets solidaires : Soutien médical, aide humanitaire, secours d'urgence.
• Projets collectifs : Projets étudiants, cagnottes d'association, achats partagés.

La création est rapide, guidée et entièrement gratuite.`
  },
  {
    id: 2,
    category: 'payment',
    icon: 'monetization_on',
    question: 'Quels sont les montants minimum/maximum pour participer ?',
    answer: `Le montant minimum de participation à une cagnotte Potify est fixé à 1,00 € (ou l'équivalent dans votre devise locale).

Il n'y a pas de plafond maximum imposé pour une contribution individuelle. Cependant, pour des raisons de conformité bancaire et anti-blanchiment, des pièces justificatives (KYC) peuvent être demandées au créateur de la cagnotte lors du versement si des seuils élevés sont atteints.`
  },
  {
    id: 3,
    category: 'security',
    icon: 'request_quote',
    question: 'Combien coûte la création d\'une cagnotte sur Potify ?',
    answer: `Créer et publier une cagnotte sur Potify est 100% GRATUIT.

Il n'y a aucun frais à l'inscription ni lors du lancement de la collecte. Pour couvrir la sécurité des transactions bancaires et le fonctionnement de la plateforme, de légers frais de gestion transparents de 1,9% + 0.30€ ne sont appliqués que lors des transferts effectifs.`
  },
  {
    id: 4,
    category: 'payment',
    icon: 'account_balance',
    question: 'Comment récupérer l\'argent collecté sur mon compte bancaire ?',
    answer: `Vous pouvez effectuer un virement vers votre compte bancaire à tout moment :
1. Rendez-vous dans votre espace 'Mes Cagnottes' > Cliquez sur 'Virement'.
2. Renseignez l'IBAN de votre compte bancaire et joignez une pièce d'identité valide.
3. Une fois la demande validée par nos équipes (généralement sous 24h), le virement SEPA est émis directement sur votre compte.`
  },
  {
    id: 5,
    category: 'security',
    icon: 'lock',
    question: 'Mes paiements et mes données bancaires sont-ils sécurisés ?',
    answer: `Oui, la sécurité est notre priorité absolue.
Toutes les transactions bancaires effectuées sur Potify sont chiffrées avec un protocole SSL 256 bits et gérées par des partenaires bancaires certifiés PCI-DSS (Stripe & PayPal). 

Vos coordonnées bancaires et numéros de carte de crédit ne sont jamais stockés sur les serveurs de Potify.`
  },
  {
    id: 6,
    category: 'creation',
    icon: 'visibility_off',
    question: 'Puis-je rendre ma cagnotte privée ou secrète ?',
    answer: `Oui ! Lors de la création ou dans les paramètres de votre cagnotte, vous pouvez régler la visibilité sur 'Privée'.

Une cagnotte privée n'apparaît pas dans les moteurs de recherche ni dans l'annuaire public. Seules les personnes à qui vous partagez le lien direct ou l'invitation pourront accéder à la page et y participer.`
  },
  {
    id: 7,
    category: 'creation',
    icon: 'share',
    question: 'Comment inviter mes proches et partager ma cagnotte ?',
    answer: `Depuis la page de votre cagnotte, cliquez sur le bouton 'Partager' pour :
• Copier le lien direct unique de votre cagnotte.
• Partager en 1 clic sur WhatsApp, Facebook, X (Twitter), et par Email.
• Envoyer des invitations par email directement depuis l'application.`
  },
  {
    id: 8,
    category: 'account',
    icon: 'contact_support',
    question: 'Comment contacter l\'équipe de support ou signaler un problème ?',
    answer: `Notre centre d'assistance dispose d'un système de chat en direct. 

Vous pouvez à tout moment cliquer sur le bouton 'Démarrer le Chat Admin' en bas de cette page pour échanger directement avec un conseiller ou un administrateur Potify.`
  }
]

const filteredFaqs = computed(() => {
  return faqs.filter(faq => {
    const matchesCategory = selectedCategory.value === 'all' || faq.category === selectedCategory.value
    const q = searchQuery.value.trim().toLowerCase()
    const matchesSearch = !q || faq.question.toLowerCase().includes(q) || faq.answer.toLowerCase().includes(q)
    return matchesCategory && matchesSearch
  })
})

function getCategoryLabel(catId) {
  const found = categories.find(c => c.id === catId)
  return found ? found.label : ''
}

function resetFilters() {
  searchQuery.value = ''
  selectedCategory.value = 'all'
}

function openHumanChat() {
  window.dispatchEvent(new CustomEvent('open-support-chat', { detail: { escalate: true } }))
}
</script>

<style scoped>
.faq-hero {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.category-pill {
  transition: all 0.2s ease-in-out;
}
.category-pill:hover {
  transform: translateY(-1px);
}

.border-grey {
  border: 1px solid #e0e0e0;
}

.faq-card {
  border-radius: 12px !important;
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}
.faq-card:hover {
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06);
}

.human-support-card {
  border-radius: 16px !important;
}

@media (max-width: 600px) {
  .full-width-sm {
    width: 100%;
  }
}
</style>
