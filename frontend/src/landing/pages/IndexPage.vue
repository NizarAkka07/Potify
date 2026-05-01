<template>
  <!-- Fond blanc comme Akkodis -->
  <q-page style="background: #FFFFFF;">

    <!-- 1. HERO SECTION – fond blanc, texte sombre, CTA jaune (style Akkodis) -->
    <div class="q-py-xl" style="background: #FFFFFF; min-height: 85vh; display: flex; align-items: center;">
      <div class="row items-center q-px-xl" style="max-width: 1200px; width: 100%; margin: 0 auto;">

        <!-- Texte gauche -->
        <div class="col-12 col-md-6 q-pr-md-xl">
          <h1 class="text-weight-bold q-mb-md" style="font-size: 2.8rem; line-height: 1.25; color: #1A1A2A;">
            Ensemble, donnons vie<br>à ce qui compte.
          </h1>
          <p class="q-mb-xl" style="font-size: 1.1rem; color: #444; line-height: 1.7;">
            Potify est la plateforme de financement participatif solidaire.
            Aidez une cause, lancez votre projet ou soutenez vos proches en quelques clics.
            Nous croyons que les solutions les plus solidaires naissent quand
            la communauté unit ses forces.
          </p>
          <div class="row q-gutter-md">
            <!-- Bouton CTA jaune Akkodis – texte sombre -->
            <q-btn
              size="md"
              no-caps
              label="Créer une cagnotte"
              style="background: #FFB300; color: #1A1A2A; font-weight: 700; padding: 12px 28px; letter-spacing: 0.5px;"
              class="shadow-2"
            />
            <q-btn
              size="md"
              outline
              no-caps
              label="Découvrir les cagnottes"
              style="color: #0D1B2E; border-color: #0D1B2E; font-weight: 600; padding: 12px 28px;"
            />
          </div>
        </div>

        <!-- Image droite -->
        <div class="col-12 col-md-6 gt-sm text-center q-pl-xl">
          <img
            src="~assets/hero-solidarity.png"
            alt="Solidarité"
            style="width: 100%; max-width: 520px; border-radius: 4px; box-shadow: 0 8px 32px rgba(0,0,0,0.12);"
          />
        </div>
      </div>
    </div>

    <!-- 2. STATS SECTION – fond gris clair comme Akkodis -->
    <div class="q-py-xl" style="background: #F5F5F5;">
      <div class="row q-col-gutter-lg q-px-xl justify-center text-center" style="max-width: 1200px; margin: 0 auto;">
        <div class="col-12 col-sm-4">
          <div class="text-h3 text-weight-bold q-mb-xs" style="color: #FFB300;">12M €</div>
          <div class="text-body1" style="color: #555;">Fonds collectés</div>
        </div>
        <div class="col-12 col-sm-4">
          <div class="text-h3 text-weight-bold q-mb-xs" style="color: #FFB300;">15 420</div>
          <div class="text-body1" style="color: #555;">Cagnottes réussies</div>
        </div>
        <div class="col-12 col-sm-4">
          <div class="text-h3 text-weight-bold q-mb-xs" style="color: #FFB300;">2.5M</div>
          <div class="text-body1" style="color: #555;">Contributeurs généreux</div>
        </div>
      </div>
    </div>

    <!-- 3. HOW IT WORKS – fond blanc -->
    <div class="q-py-xl" style="background: #FFFFFF;">
      <div style="max-width: 1200px; margin: 0 auto;" class="q-px-xl">
        <h2 class="text-h4 text-weight-bold q-mb-xl" style="color: #1A1A2A;">Comment ça marche ?</h2>
        <div class="row q-col-gutter-xl">
          <div class="col-12 col-md-4">
            <div class="q-mb-md" style="width: 48px; height: 4px; background: #FFB300;"></div>
            <div class="text-h6 text-weight-bold q-mb-sm" style="color: #1A1A2A;">1. Créer une cagnotte</div>
            <p style="color: #555; line-height: 1.7;">Décrivez votre besoin, fixez un objectif et personnalisez votre page en 2 minutes.</p>
          </div>
          <div class="col-12 col-md-4">
            <div class="q-mb-md" style="width: 48px; height: 4px; background: #FFB300;"></div>
            <div class="text-h6 text-weight-bold q-mb-sm" style="color: #1A1A2A;">2. Partager autour de vous</div>
            <p style="color: #555; line-height: 1.7;">Partagez le lien avec votre famille, vos amis et sur les réseaux sociaux.</p>
          </div>
          <div class="col-12 col-md-4">
            <div class="q-mb-md" style="width: 48px; height: 4px; background: #FFB300;"></div>
            <div class="text-h6 text-weight-bold q-mb-sm" style="color: #1A1A2A;">3. Recevoir les fonds</div>
            <p style="color: #555; line-height: 1.7;">Récupérez l'argent collecté de manière sécurisée et transparente.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 4. CAGNOTTES POPULAIRES – fond gris clair -->
    <div class="q-py-xl" style="background: #F5F5F5;">
      <div style="max-width: 1200px; margin: 0 auto;" class="q-px-xl">
        <div class="row items-center justify-between q-mb-xl">
          <h2 class="text-h4 text-weight-bold q-my-none" style="color: #1A1A2A;">Cagnottes solidaires</h2>
          <q-btn flat no-caps style="color: #1E6BE6; font-weight: 600;" label="Voir tout →" />
        </div>
        <div class="row q-col-gutter-lg">
          <div class="col-12 col-sm-6 col-md-4" v-for="n in 3" :key="n">
            <q-card class="shadow-2 hover-card" style="border-radius: 4px; border: 1px solid #E0E0E0;">
              <img
                src="https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"
                style="height: 200px; object-fit: cover; width: 100%;"
              />
              <q-card-section>
                <div class="text-caption text-weight-bold q-mb-xs" style="color: #FFB300; text-transform: uppercase; letter-spacing: 1px;">Santé & Urgence</div>
                <div class="text-subtitle1 text-weight-bold q-mb-xs line-clamp-2" style="color: #1A1A2A;">Soutenons le traitement médical de la petite Emma</div>
                <div class="text-caption" style="color: #888;">Par Association Espoir</div>
              </q-card-section>
              <q-card-section class="q-pt-none">
                <q-linear-progress :value="0.75" style="height: 6px; border-radius: 3px;" color="primary" track-color="grey-3" class="q-mb-sm" />
                <div class="row justify-between text-caption text-weight-bold">
                  <span style="color: #1A1A2A;">15 000 € collectés</span>
                  <span style="color: #888;">sur 20 000 €</span>
                </div>
              </q-card-section>
              <q-card-actions class="q-pa-md">
                <q-btn
                  no-caps
                  class="full-width"
                  label="Contribuer"
                  style="background: #FFB300; color: #1A1A2A; font-weight: 700;"
                />
              </q-card-actions>
            </q-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 5. CATEGORIES – fond blanc -->
    <div class="q-py-xl" style="background: #FFFFFF;">
      <div style="max-width: 1200px; margin: 0 auto;" class="q-px-xl">
        <h2 class="text-h4 text-weight-bold q-mb-xl" style="color: #1A1A2A;">Trouvez une cause qui vous parle</h2>
        <div class="row q-col-gutter-md">
          <div class="col-6 col-sm-4 col-md-2" v-for="cat in categories" :key="cat.name">
            <q-card
              class="q-pa-lg text-center cursor-pointer cat-card"
              style="border: 1px solid #E0E0E0; border-radius: 4px; transition: all 0.2s ease;"
            >
              <q-icon :name="cat.icon" size="2.5rem" class="q-mb-sm" style="color: #0D1B2E;" />
              <div class="text-weight-bold" style="color: #1A1A2A; font-size: 0.85rem;">{{ cat.name }}</div>
            </q-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 6. TÉMOIGNAGES – fond gris clair -->
    <div class="q-py-xl" style="background: #F5F5F5;">
      <div style="max-width: 900px; margin: 0 auto;" class="q-px-xl">
        <h2 class="text-h4 text-weight-bold q-mb-xl" style="color: #1A1A2A;">Ils font confiance à Potify</h2>
        <q-carousel
          v-model="slide"
          transition-prev="slide-right"
          transition-next="slide-left"
          swipeable animated navigation padding arrows
          height="280px"
          style="background: #FFFFFF; border-radius: 4px; border: 1px solid #E0E0E0;"
          control-color="primary"
        >
          <q-carousel-slide name="1" class="column no-wrap flex-center q-pa-xl">
            <div class="text-h6 text-italic q-mb-lg" style="color: #333; line-height: 1.6;">
              "Grâce à Potify, nous avons pu récolter les fonds nécessaires pour reconstruire notre refuge animalier en un temps record."
            </div>
            <div class="row items-center q-gutter-md">
              <q-avatar size="48px"><img src="https://cdn.quasar.dev/img/avatar2.jpg"></q-avatar>
              <div>
                <div class="text-weight-bold" style="color: #1A1A2A;">Marie L.</div>
                <div class="text-caption" style="color: #888;">Directrice de Refuge</div>
              </div>
            </div>
          </q-carousel-slide>
          <q-carousel-slide name="2" class="column no-wrap flex-center q-pa-xl">
            <div class="text-h6 text-italic q-mb-lg" style="color: #333; line-height: 1.6;">
              "Simple, rapide et transparent. Créer ma cagnotte pour financer mon projet de fin d'études a été une expérience parfaite."
            </div>
            <div class="row items-center q-gutter-md">
              <q-avatar size="48px"><img src="https://cdn.quasar.dev/img/avatar3.jpg"></q-avatar>
              <div>
                <div class="text-weight-bold" style="color: #1A1A2A;">Thomas D.</div>
                <div class="text-caption" style="color: #888;">Étudiant</div>
              </div>
            </div>
          </q-carousel-slide>
        </q-carousel>
      </div>
    </div>

    <!-- 7. RECHERCHE – fond blanc -->
    <div class="q-py-xl" style="background: #FFFFFF;">
      <div style="max-width: 800px; margin: 0 auto;" class="q-px-xl">
        <h3 class="text-h5 text-weight-bold q-mb-sm" style="color: #1A1A2A;">Chercher une cagnotte spécifique</h3>
        <p class="q-mb-md" style="color: #555;">Trouvez une cause qui vous correspond parmi des milliers de cagnottes.</p>
        <q-input
          outlined
          v-model="search"
          label="Rechercher (ex: Animaux, École, Prénom...)"
          style="background: #FFFFFF;"
          color="secondary"
        >
          <template v-slot:append>
            <q-icon v-if="search !== ''" name="close" @click="search = ''" class="cursor-pointer" style="color: #888;" />
            <q-icon name="search" style="color: #0D1B2E;" />
          </template>
        </q-input>
      </div>
    </div>

    <!-- 8. CTA FINAL – fond dark navy Akkodis -->
    <div class="q-py-xl text-white text-center" style="background: #0D1B2E;">
      <div style="max-width: 800px; margin: 0 auto;" class="q-px-xl">
        <h2 class="text-h4 text-weight-bold q-mb-md" style="color: #FFFFFF;">Prêt à faire la différence ?</h2>
        <p class="text-body1 q-mb-xl" style="color: rgba(255,255,255,0.75); line-height: 1.7;">
          Lancez votre cagnotte aujourd'hui et mobilisez votre communauté autour de ce qui vous tient à cœur.
        </p>
        <q-btn
          size="lg"
          no-caps
          label="Lancer ma cagnotte maintenant"
          icon-right="rocket_launch"
          style="background: #FFB300; color: #1A1A2A; font-weight: 700; padding: 14px 36px; letter-spacing: 0.5px;"
          class="shadow-4"
        />
      </div>
    </div>

  </q-page>
</template>

<script setup>
import { ref } from 'vue'

const slide = ref('1')
const search = ref('')

const categories = [
  { name: 'Santé', icon: 'local_hospital' },
  { name: 'Éducation', icon: 'school' },
  { name: 'Urgence', icon: 'warning' },
  { name: 'Animaux', icon: 'pets' },
  { name: 'Projets', icon: 'flight_takeoff' },
  { name: 'Sport', icon: 'directions_run' }
]
</script>

<style scoped>
/* Hover card cagnotte */
.hover-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.hover-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
  border-color: #FFB300 !important;
}

/* Hover catégorie */
.cat-card:hover {
  border-color: #FFB300 !important;
  box-shadow: 0 4px 12px rgba(255, 179, 0, 0.2);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
