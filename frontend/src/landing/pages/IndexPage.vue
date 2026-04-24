<template>
  <q-page class="bg-grey-1">
    
    <!-- 1. HERO SECTION -->
    <div class="bg-primary text-white q-py-xl flex flex-center" style="min-height: 80vh; background: linear-gradient(135deg, #1976D2 0%, #0D47A1 100%);">
      <div class="row items-center q-px-lg" style="max-width: 1200px; width: 100%;">
        <div class="col-12 col-md-6 q-pr-md-xl text-center text-md-left">
          <h1 class="text-weight-bolder q-mb-md" style="font-size: 3.5rem; line-height: 1.2;">
            Ensemble, donnons vie à ce qui compte.
          </h1>
          <p class="text-weight-regular q-mb-xl" style="font-size: 1.25rem; opacity: 0.9;">
            Rejoignez Potify, la plateforme de financement participatif solidaire. Aidez une cause, lancez votre projet ou soutenez vos proches en quelques clics.
          </p>
          <div class="row q-gutter-md justify-center justify-md-start">
            <q-btn size="lg" color="white" text-color="primary" class="text-weight-bold shadow-3" no-caps label="Créer une cagnotte" />
            <q-btn size="lg" outline color="white" class="text-weight-bold" no-caps label="Découvrir les cagnottes" />
          </div>
        </div>
        <div class="col-12 col-md-6 gt-sm text-center">
          <!-- Illustration or Image placeholder -->
          <img src="https://images.unsplash.com/photo-1593113563332-f148e42f9b84?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" alt="Solidarité" style="width: 100%; max-width: 500px; border-radius: 8px; border: 4px solid rgba(255, 255, 255, 0.2); box-shadow: 0 4px 15px rgba(0,0,0,0.2);" />
        </div>
      </div>
    </div>

    <!-- 2. TRUST / STATS SECTION -->
    <div class="q-py-xl bg-white">
      <div class="row q-col-gutter-lg q-px-lg justify-center text-center" style="max-width: 1200px; margin: 0 auto; margin-top: -60px; position: relative; z-index: 10;">
        <div class="col-12 col-sm-4">
          <q-card class="shadow-4 rounded-borders q-pa-lg">
            <q-icon name="attach_money" color="primary" size="3rem" class="q-mb-md" />
            <div class="text-h4 text-weight-bolder text-primary">12M €</div>
            <div class="text-subtitle1 text-grey-7">Fonds collectés</div>
          </q-card>
        </div>
        <div class="col-12 col-sm-4">
          <q-card class="shadow-4 rounded-borders q-pa-lg">
            <q-icon name="favorite" color="secondary" size="3rem" class="q-mb-md" />
            <div class="text-h4 text-weight-bolder text-primary">15 420</div>
            <div class="text-subtitle1 text-grey-7">Cagnottes réussies</div>
          </q-card>
        </div>
        <div class="col-12 col-sm-4">
          <q-card class="shadow-4 rounded-borders q-pa-lg">
            <q-icon name="people" color="accent" size="3rem" class="q-mb-md" />
            <div class="text-h4 text-weight-bolder text-primary">2.5M</div>
            <div class="text-subtitle1 text-grey-7">Contributeurs généreux</div>
          </q-card>
        </div>
      </div>
    </div>

    <!-- 3. HOW IT WORKS -->
    <div class="q-py-xl bg-grey-1 text-center">
      <div style="max-width: 1200px; margin: 0 auto;" class="q-px-lg">
        <h2 class="text-h3 text-weight-bold text-dark q-mb-xl">Comment ça marche ?</h2>
        <div class="row q-col-gutter-xl">
          <div class="col-12 col-md-4">
            <q-avatar size="100px" font-size="50px" color="blue-1" text-color="primary" class="q-mb-md shadow-2">
              <q-icon name="create" />
            </q-avatar>
            <div class="text-h5 text-weight-bold q-mb-sm">1. Créer une cagnotte</div>
            <p class="text-grey-7 text-body1">Décrivez votre besoin, fixez un objectif et personnalisez votre page en 2 minutes.</p>
          </div>
          <div class="col-12 col-md-4">
            <q-avatar size="100px" font-size="50px" color="blue-1" text-color="primary" class="q-mb-md shadow-2">
              <q-icon name="share" />
            </q-avatar>
            <div class="text-h5 text-weight-bold q-mb-sm">2. Partager autour de vous</div>
            <p class="text-grey-7 text-body1">Partagez le lien avec votre famille, vos amis et sur les réseaux sociaux.</p>
          </div>
          <div class="col-12 col-md-4">
            <q-avatar size="100px" font-size="50px" color="blue-1" text-color="primary" class="q-mb-md shadow-2">
              <q-icon name="redeem" />
            </q-avatar>
            <div class="text-h5 text-weight-bold q-mb-sm">3. Recevoir les fonds</div>
            <p class="text-grey-7 text-body1">Récupérez l'argent collecté de manière sécurisée et transparente.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 4. POPULAR FUNDRAISERS -->
    <div class="q-py-xl bg-white">
      <div style="max-width: 1200px; margin: 0 auto;" class="q-px-lg">
        <div class="row items-center justify-between q-mb-xl">
          <h2 class="text-h3 text-weight-bold text-dark q-my-none">Cagnottes solidaires</h2>
          <q-btn flat color="primary" no-caps label="Voir tout" icon-right="arrow_forward" />
        </div>
        
        <div class="row q-col-gutter-lg">
          <div class="col-12 col-sm-6 col-md-4" v-for="n in 3" :key="n">
            <q-card class="my-card cursor-pointer rounded-borders shadow-3 hover-lift">
              <img :src="`https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80`" style="height: 200px; object-fit: cover;" />
              <q-card-section>
                <div class="text-overline text-primary text-weight-bold">Santé & Urgence</div>
                <div class="text-h6 text-weight-bold q-mt-sm q-mb-xs line-clamp-2">Soutenons le traitement médical de la petite Emma</div>
                <div class="text-caption text-grey-7">Par Association Espoir</div>
              </q-card-section>
              <q-card-section class="q-pt-none">
                <q-linear-progress :value="0.75" color="primary" size="10px" class="rounded-borders q-mb-sm" />
                <div class="row justify-between text-body2 text-weight-bold">
                  <span class="text-primary">15 000 € collectés</span>
                  <span class="text-grey-6">sur 20 000 €</span>
                </div>
              </q-card-section>
              <q-separator />
              <q-card-actions align="center" class="q-pa-md">
                <q-btn color="primary" class="full-width text-weight-bold" no-caps label="Contribuer" />
              </q-card-actions>
            </q-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 5. CATEGORIES -->
    <div class="q-py-xl bg-grey-1 text-center">
      <div style="max-width: 1200px; margin: 0 auto;" class="q-px-lg">
        <h2 class="text-h4 text-weight-bold text-dark q-mb-xl">Trouvez une cause qui vous parle</h2>
        <div class="row q-col-gutter-md justify-center">
          <div class="col-6 col-sm-4 col-md-2" v-for="cat in categories" :key="cat.name">
            <q-card class="q-pa-md cursor-pointer hover-bg-primary transition-all">
              <q-icon :name="cat.icon" size="3rem" :color="cat.color" class="q-mb-sm icon-transition" />
              <div class="text-weight-bold text-grey-9 title-transition">{{ cat.name }}</div>
            </q-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 6. TESTIMONIALS -->
    <div class="q-py-xl bg-white text-center">
      <div style="max-width: 900px; margin: 0 auto;" class="q-px-lg">
        <h2 class="text-h3 text-weight-bold text-dark q-mb-xl">Ils font confiance à Potify</h2>
        
        <q-carousel
          v-model="slide"
          transition-prev="slide-right"
          transition-next="slide-left"
          swipeable
          animated
          control-color="primary"
          navigation
          padding
          arrows
          height="300px"
          class="bg-white rounded-borders"
        >
          <q-carousel-slide name="1" class="column no-wrap flex-center">
            <q-icon name="format_quote" color="grey-3" size="5rem" class="absolute-top" style="z-index: 0" />
            <div class="q-mt-md text-h5 text-italic text-grey-8 relative-position" style="z-index: 1">
              "Grâce à Potify, nous avons pu récolter les fonds nécessaires pour reconstruire notre refuge animalier en un temps record. Une communauté incroyable !"
            </div>
            <q-avatar size="60px" class="q-mt-xl shadow-2">
              <img src="https://cdn.quasar.dev/img/avatar2.jpg">
            </q-avatar>
            <div class="q-mt-sm text-weight-bold text-dark">Marie L.</div>
            <div class="text-caption text-grey">Directrice de Refuge</div>
          </q-carousel-slide>

          <q-carousel-slide name="2" class="column no-wrap flex-center">
            <q-icon name="format_quote" color="grey-3" size="5rem" class="absolute-top" style="z-index: 0" />
            <div class="q-mt-md text-h5 text-italic text-grey-8 relative-position" style="z-index: 1">
              "Simple, rapide et transparent. Créer ma cagnotte pour financer mon projet de fin d'études a été une expérience parfaite."
            </div>
            <q-avatar size="60px" class="q-mt-xl shadow-2">
              <img src="https://cdn.quasar.dev/img/avatar3.jpg">
            </q-avatar>
            <div class="q-mt-sm text-weight-bold text-dark">Thomas D.</div>
            <div class="text-caption text-grey">Étudiant</div>
          </q-carousel-slide>
        </q-carousel>
      </div>
    </div>

    <!-- 7. SEARCH & DISCOVERY -->
    <div class="q-py-xl bg-blue-1">
      <div style="max-width: 800px; margin: 0 auto;" class="q-px-lg text-center">
        <h3 class="text-h4 text-weight-bold text-primary q-mb-md">Chercher une cagnotte spécifique</h3>
        <q-input
          outlined
          bottom-slots
          v-model="search"
          label="Rechercher (ex: Animaux, École, Prénom...)"
          class="bg-white rounded-borders shadow-2 q-mb-md text-h6"
        >
          <template v-slot:append>
            <q-icon v-if="search !== ''" name="close" @click="search = ''" class="cursor-pointer" />
            <q-icon name="search" />
          </template>
        </q-input>
      </div>
    </div>

    <!-- 8. CTA SECTION -->
    <div class="q-py-xl bg-primary text-white text-center">
      <div style="max-width: 800px; margin: 0 auto;" class="q-px-lg">
        <h2 class="text-h3 text-weight-bold q-mb-md">Prêt à faire la différence ?</h2>
        <p class="text-h6 text-weight-regular opacity-80 q-mb-xl">Lancez votre cagnotte aujourd'hui et mobilisez votre communauté autour de ce qui vous tient à cœur.</p>
        <q-btn size="xl" color="white" text-color="primary" class="text-weight-bolder shadow-5" no-caps label="Lancer ma cagnotte maintenant" icon-right="rocket_launch" />
      </div>
    </div>

  </q-page>
</template>

<script setup>
import { ref } from 'vue'

const slide = ref('1')
const search = ref('')

const categories = [
  { name: 'Santé', icon: 'local_hospital', color: 'red' },
  { name: 'Éducation', icon: 'school', color: 'blue' },
  { name: 'Urgence', icon: 'warning', color: 'orange' },
  { name: 'Animaux', icon: 'pets', color: 'brown' },
  { name: 'Projets', icon: 'flight_takeoff', color: 'purple' },
  { name: 'Sport', icon: 'directions_run', color: 'green' }
]
</script>

<style scoped>
.hero-section {
  min-height: 80vh;
  background: linear-gradient(135deg, #1976D2 0%, #0D47A1 100%);
}

.hero-img {
  width: 100%;
  max-width: 500px;
  border: 4px solid rgba(255, 255, 255, 0.2);
}

.hover-lift {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.hover-lift:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.15) !important;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;  
  overflow: hidden;
}

.hover-bg-primary:hover {
  background-color: var(--q-primary);
}

.hover-bg-primary:hover .title-transition {
  color: white !important;
}

.hover-bg-primary:hover .icon-transition {
  color: white !important;
}

.transition-all {
  transition: all 0.3s ease;
}

.opacity-80 {
  opacity: 0.8;
}
</style>
