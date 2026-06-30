<template>
  <q-page :style="{ background: $q.dark.isActive ? '#0A111E' : '#FFFFFF' }">

    <!-- 1. HERO SECTION – Solidarity Design as in tadamon.jpeg -->
    <div class="hero-section relative-position overflow-hidden" :style="{ background: $q.dark.isActive ? '#0A111E' : '#FFFFFF', height: '92vh', minHeight: '680px', display: 'flex', alignItems: 'center', justifyContent: 'center' }">
      
      <!-- Solidarity Hands Image on the Right -->
      <div class="gt-xs hero-floating-image" :style="{ position: 'absolute', top: 0, right: locale === 'ar' ? 'auto' : '0', left: locale === 'ar' ? '0' : 'auto', width: '62%', height: '100%', zIndex: 0 }">
        <img
          src="~assets/tadamon1.jpeg"
          :style="{ width: '100%', height: '100%', objectFit: 'cover', transform: locale === 'ar' ? 'scaleX(-1)' : 'none' }"
          alt="Solidarity Hands"
        />
        <!-- Gradient Mask to blend image into the white/dark background -->
        <div
          :style="{
            background: $q.dark.isActive
              ? (locale === 'ar'
                ? 'linear-gradient(to left, #0A111E 0%, rgba(10,17,30,0.85) 30%, rgba(10,17,30,0) 100%)'
                : 'linear-gradient(to right, #0A111E 0%, rgba(10,17,30,0.85) 30%, rgba(10,17,30,0) 100%)')
              : (locale === 'ar'
                ? 'linear-gradient(to left, #FFFFFF 0%, rgba(255,255,255,0.85) 30%, rgba(255,255,255,0) 100%)'
                : 'linear-gradient(to right, #FFFFFF 0%, rgba(255,255,255,0.85) 30%, rgba(255,255,255,0) 100%)'),
            position: 'absolute',
            top: 0,
            left: locale === 'ar' ? 'auto' : '-1px',
            right: locale === 'ar' ? '-1px' : 'auto',
            width: '101%',
            height: '100%',
            pointerEvents: 'none'
          }"
        ></div>
      </div>

      <!-- Hero Content -->
      <div class="q-px-xl max-container fade-up-text" :class="locale === 'ar' ? 'text-right' : 'text-left'" :style="{ direction: locale === 'ar' ? 'rtl' : 'ltr' }" style="z-index: 2; width: 100%;">
        <div class="row items-center">
          <div class="col-12 col-md-7" style="padding-top: 20px;">
            <!-- Badge -->
            <div class="q-mb-lg">
              <span class="simple-badge badge-premium" :style="{ background: 'rgba(22, 163, 74, 0.06)', border: '1px solid rgba(22, 163, 74, 0.15)', borderRadius: '20px', padding: '6px 14px', display: 'inline-flex', alignItems: 'center' }">
                <span class="badge-dot" style="background: #16A34A; width: 8px; height: 8px; border-radius: 50%; margin-inline-end: 8px;"></span>
                <span class="badge-text" :style="{ color: $q.dark.isActive ? '#4ade80' : '#166534' }" style="font-size: 0.85rem; font-weight: 600; text-transform: none; letter-spacing: normal;">{{ $t('home.heroBadge') }}</span>
              </span>
            </div>

            <!-- Title -->
            <h1 class="text-weight-bold q-mb-md hero-title" :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#166534' }" style="font-size: 4rem; line-height: 1.15; font-family: 'Playfair Display', serif; letter-spacing: -1px; margin-top: 0;">
              {{ $t('home.title') }}
            </h1>

            <!-- Subtitle -->
            <p class="text-body1 q-mb-xl" :style="{ color: $q.dark.isActive ? 'rgba(255,255,255,0.7)' : 'rgba(15,41,66,0.8)' }" style="font-size: 1.05rem; line-height: 1.75; max-width: 520px; font-weight: 400; letter-spacing: -0.1px;">
              {{ $t('home.subtitle') }}
            </p>

            <!-- Buttons -->
            <div class="row q-gutter-md">
              <q-btn
                unelevated
                no-caps
                class="btn-hero-primary text-white"
                style="padding: 14px 36px; border-radius: 12px; font-weight: 600; font-size: 0.95rem; box-shadow: 0 4px 18px rgba(22, 163, 74, 0.2);"
                to="/pools/create"
              >
                <q-icon name="favorite_border" size="18px" :class="locale === 'ar' ? 'q-ml-sm' : 'q-mr-sm'" />
                {{ $t('home.ctaButton') }}
              </q-btn>
              <q-btn
                unelevated
                no-caps
                class="btn-hero-secondary"
                :style="{
                  background: $q.dark.isActive ? '#1E293B' : '#FFFFFF',
                  border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.15)' : '1px solid rgba(22,163,74,0.2)',
                  color: $q.dark.isActive ? '#FFFFFF' : '#166534'
                }"
                style="padding: 14px 28px; border-radius: 12px; font-weight: 600; font-size: 0.95rem;"
                @click="scrollToHowItWorks"
              >
                <q-icon name="play_circle_outline" size="20px" :class="locale === 'ar' ? 'q-ml-sm' : 'q-mr-sm'" style="color: #16A34A;" />
                {{ $t('home.learnMore') }}
              </q-btn>
            </div>
          </div>
          <!-- Right side placeholder spacer to balance the layout columns -->
          <div class="gt-xs col-md-5"></div>
        </div>
      </div>
    </div>

    <!-- 2. STATS BAR – Floating Card as in tadamon.jpeg -->
    <div class="max-container q-px-xl relative-position" style="z-index: 10; margin-top: -60px; margin-bottom: 20px;">
      <q-card
        class="flat q-py-xl q-px-xl"
        :style="{
          background: $q.dark.isActive ? '#1E293B' : '#FFFFFF',
          borderRadius: '24px',
          border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.1)' : '1px solid rgba(22,163,74,0.08)',
          boxShadow: $q.dark.isActive ? 'none' : '0 10px 40px rgba(0,0,0,0.03)'
        }"
      >
        <div class="row q-col-gutter-lg items-center justify-center text-center">
          <!-- Stat 1 -->
          <div class="col-12 col-sm-4 row items-center justify-center q-gutter-md" :style="{ flexDirection: locale === 'ar' ? 'row-reverse' : 'row' }">
            <q-avatar size="48px" style="background: rgba(22, 163, 74, 0.06);">
              <q-icon name="account_balance" size="24px" style="color: #16A34A;" />
            </q-avatar>
            <div :class="locale === 'ar' ? 'text-right' : 'text-left'">
              <div class="text-h4 text-weight-bold" :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#166534' }" style="font-family: 'Playfair Display', serif; font-size: 2.2rem; line-height: 1.1;">{{ collectedCount }}M €</div>
              <div class="text-caption text-grey-6" style="font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px;">{{ $t('home.statsCollected') }}</div>
            </div>
          </div>

          <!-- Stat 2 -->
          <div class="col-12 col-sm-4 row items-center justify-center q-gutter-md" :style="{ borderLeft: $q.dark.isActive ? '1px solid rgba(255, 255, 255, 0.1)' : '1px solid rgba(22, 163, 74, 0.1)', borderRight: $q.dark.isActive ? '1px solid rgba(255, 255, 255, 0.1)' : '1px solid rgba(22, 163, 74, 0.1)', flexDirection: locale === 'ar' ? 'row-reverse' : 'row' }">
            <q-avatar size="48px" style="background: rgba(22, 163, 74, 0.06);">
              <q-icon name="groups" size="24px" style="color: #16A34A;" />
            </q-avatar>
            <div :class="locale === 'ar' ? 'text-right' : 'text-left'">
              <div class="text-h4 text-weight-bold" :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#166534' }" style="font-family: 'Playfair Display', serif; font-size: 2.2rem; line-height: 1.1;">{{ poolsCount.toLocaleString('fr-FR') }}+</div>
              <div class="text-caption text-grey-6" style="font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px;">{{ $t('home.statsProjects') }}</div>
            </div>
          </div>

          <!-- Stat 3 -->
          <div class="col-12 col-sm-4 row items-center justify-center q-gutter-md" :style="{ flexDirection: locale === 'ar' ? 'row-reverse' : 'row' }">
            <q-avatar size="48px" style="background: rgba(22, 163, 74, 0.06);">
              <q-icon name="favorite_border" size="24px" style="color: #16A34A;" />
            </q-avatar>
            <div :class="locale === 'ar' ? 'text-right' : 'text-left'">
              <div class="text-h4 text-weight-bold" :style="{ color: $q.dark.isActive ? '#FFFFFF' : '#166534' }" style="font-family: 'Playfair Display', serif; font-size: 2.2rem; line-height: 1.1;">{{ contributorsCount }}M</div>
              <div class="text-caption text-grey-6" style="font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px;">{{ $t('home.statsMembers') }}</div>
            </div>
          </div>
        </div>
      </q-card>
    </div>

    <!-- 3. TOUTES LES CATÉGORIES – Dynamic Grid -->
    <div class="q-py-xxl" :style="{ background: $q.dark.isActive ? '#090E17' : '#FFFFFF', padding: '100px 0' }">
      <div class="max-container q-px-xl">
        <div class="text-center q-mb-xxl">
          <div class="text-overline text-weight-bold text-primary q-mb-xs" style="letter-spacing: 2px; color: #16A34A !important;">
            {{ $t('home.browse') }}
          </div>
          <h2 class="text-h3 text-weight-bold q-mb-sm" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Playfair Display', serif; font-style: italic; letter-spacing: -0.5px;">
            {{ $t('home.allCategoriesTitle') }}
          </h2>
          <p :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-7'" style="font-size: 1.05rem; font-weight: 300;">
            {{ $t('home.allCategoriesDesc') }}
          </p>
        </div>

        <div class="row q-col-gutter-lg">
          <div 
            class="col-12 col-sm-6 col-md-3" 
            v-for="cat in categories" 
            :key="cat.name"
          >
            <div 
              class="category-dark-card q-pa-xl cursor-pointer"
              @click="$router.push({ path: '/pools', query: { category: cat.name } })"
              :style="{
                background: $q.dark.isActive ? 'rgba(255, 255, 255, 0.02)' : '#FFFFFF',
                border: $q.dark.isActive ? '1px solid rgba(255, 255, 255, 0.06)' : '1px solid #f1f5f9',
                borderRadius: '24px',
                boxShadow: $q.dark.isActive ? 'none' : '0 4px 20px rgba(0,0,0,0.015)'
              }"
            >
              <div class="row items-center justify-between q-mb-xl">
                <div 
                  class="category-icon-box flex flex-center" 
                  :style="{ 
                    background: $q.dark.isActive ? 'rgba(255, 255, 255, 0.04)' : cat.color,
                    color: cat.iconColor
                  }"
                  style="width: 56px; height: 56px; border-radius: 50%;"
                >
                  <div style="width: 28px; height: 28px; display: flex; align-items: center; justify-content: center;" v-html="cat.svg"></div>
                </div>
                <div 
                  class="category-arrow-btn flex flex-center"
                  :style="{
                    border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.15)' : '1px solid #f1f5f9',
                    background: $q.dark.isActive ? 'rgba(255, 255, 255, 0.02)' : '#FFFFFF'
                  }"
                  style="width: 36px; height: 36px; border-radius: 50%; transition: all 0.3s ease;"
                >
                  <q-icon name="arrow_forward" size="16px" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'" />
                </div>
              </div>
              <div class="text-weight-bold text-h6 q-mb-xs" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Inter', sans-serif;">{{ getCategoryLabel(cat.name) }}</div>
              <div style="font-size: 0.9rem;" class="text-weight-medium">
                <span :style="{ color: cat.iconColor }" class="q-mr-xs">{{ cat.countNum }}</span>
                <span :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'">{{ $t('publicPools.poolsFoundMultiple') }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 4. CAGNOTTES POPULAIRES – Dynamic Database Grid -->
    <div class="q-py-xxl" :style="{ background: $q.dark.isActive ? '#0A1120' : '#f8fafc', padding: '100px 0', borderTop: $q.dark.isActive ? '1px solid rgba(255,255,255,0.03)' : '1px solid rgba(0,0,0,0.05)' }">
      <div class="max-container q-px-xl">
        <div class="row items-end justify-between q-mb-xxl">
          <div>
            <div class="text-overline text-weight-bold text-primary q-mb-xs" style="letter-spacing: 2px; color: #16A34A !important;">
              {{ $t('home.discover') }}
            </div>
            <h2 class="text-h3 text-weight-bold q-mb-none" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Playfair Display', serif; font-style: italic; letter-spacing: -0.5px; margin-top: 0;">
              {{ $t('home.popularCampaigns') }}
            </h2>
            <p :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-7'" class="q-mt-sm q-mb-none" style="font-size: 1.05rem; font-weight: 300;">
              {{ $t('home.popularCampaignsDesc') }}
            </p>
          </div>
          <q-btn 
            flat 
            no-caps 
            color="primary"
            :label="$t('home.viewAllExplorer')" 
            to="/pools" 
            icon-right="arrow_forward"
            style="font-weight: 600;"
            :class="$q.dark.isActive ? 'text-white' : 'text-primary'"
          />
        </div>

        <div class="row q-col-gutter-lg">
          <!-- Real dynamic campaign cards -->
          <div class="col-12 col-sm-6 col-md-4" v-for="pool in pools" :key="pool.id">
            <q-card 
              class="premium-pool-card flat shadow-0 cursor-pointer" 
              :style="{ 
                borderRadius: '20px', 
                background: $q.dark.isActive ? 'rgba(255,255,255,0.03)' : '#ffffff', 
                border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(0,0,0,0.06)',
                overflow: 'hidden' 
              }" 
              @click="contribute(pool)"
            >
              <!-- Image or generic emoji block -->
              <div 
                v-if="pool.imageUrl"
                style="height: 200px; width: 100%; background: #ECEFF1;" 
                class="relative-position"
              >
                <img
                  :src="pool.imageUrl"
                  style="height: 200px; object-fit: cover; width: 100%;"
                  alt="Campaign Image"
                />
                <div class="absolute-top-left q-ma-md">
                  <span class="custom-card-tag" :style="{ background: pool.tagBg || '#E2E8F0', color: pool.tagColor || '#475569' }">
                    {{ getTagLabel(pool.tag) }}
                  </span>
                </div>
              </div>
              <div 
                v-else
                :style="{ background: pool.cardBg || ($q.dark.isActive ? 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)' : 'linear-gradient(135deg, #e2e8f0 0%, #f1f5f9 100%)') }" 
                style="height: 200px; width: 100%;" 
                class="relative-position flex flex-center"
              >
                <div style="font-size: 4rem;">{{ pool.icon || '🌱' }}</div>
                <div class="absolute-top-left q-ma-md">
                  <span class="custom-card-tag" :style="{ background: pool.tagBg || '#16A34A', color: pool.tagColor || '#062f25' }">
                    {{ getTagLabel(pool.tag) }}
                  </span>
                </div>
              </div>

              <!-- Card Body -->
              <q-card-section class="q-pa-lg">
                <!-- User Avatar Block -->
                <div class="row items-center q-mb-md">
                  <q-avatar size="28px" :style="{ background: pool.avatarBg || '#16A34A' }" class="text-white text-weight-bold q-mr-sm" style="font-size: 0.75rem;">
                    {{ pool.ownerInitials || 'P' }}
                  </q-avatar>
                  <span class="text-caption text-weight-medium" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'">{{ pool.ownerName || $t('home.memberPotify') }}</span>
                </div>

                <h3 class="text-subtitle1 text-weight-bold q-mb-sm line-clamp-2" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Inter', sans-serif; line-height: 1.35; height: 46px; margin-top:0;">
                  {{ pool.title }}
                </h3>
                <p class="text-caption line-clamp-2 q-mb-lg" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" style="height: 36px; line-height: 1.4;">
                  {{ pool.description }}
                </p>

                <!-- Progress Section -->
                <div class="q-mb-md">
                  <div class="row justify-between items-center text-caption text-weight-bold q-mb-xs">
                     <span class="text-weight-bold" :class="$q.dark.isActive ? 'text-white' : 'text-dark'">{{ getPercentageText(pool) }}</span>
                     <span :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'">{{ pool.currentAmount }} {{ pool.currency || 'MAD' }} <span :class="$q.dark.isActive ? 'text-grey-6' : 'text-grey-5'" class="text-weight-regular">/ {{ pool.goalAmount }} {{ pool.currency || 'MAD' }}</span></span>
                  </div>
                  <q-linear-progress :value="getProgressValue(pool)" style="height: 6px; border-radius: 99px;" color="primary" class="progress-bar-neon" />
                </div>
              </q-card-section>

              <!-- Card Footer -->
              <q-card-section class="q-px-lg q-pb-lg q-pt-none row items-center justify-between" :style="{ borderTop: $q.dark.isActive ? '1px solid rgba(255,255,255,0.04)' : '1px solid rgba(0,0,0,0.05)' }">
                <div class="text-caption row items-center" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'">
                  <q-icon name="people" size="16px" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" class="q-mr-xs" />
                  {{ pool.contributors || 12 }} {{ $t('home.contributors') }}
                </div>
                <div class="text-caption row items-center" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'">
                  <q-icon name="schedule" size="16px" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" class="q-mr-xs" />
                  {{ pool.daysRemaining || 10 }}{{ $t('home.days') }} {{ $t('home.daysRemaining') }}
                </div>
              </q-card-section>
            </q-card>
          </div>

          <!-- Mock campaigns when API is empty / fallback -->
          <template v-if="pools.length === 0">
            <div class="col-12 col-sm-6 col-md-4" v-for="mock in mockPools" :key="mock.id">
              <q-card 
                class="premium-pool-card flat shadow-0 cursor-pointer" 
                :style="{ 
                  borderRadius: '20px', 
                  background: $q.dark.isActive ? 'rgba(255,255,255,0.03)' : '#ffffff', 
                  border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(0,0,0,0.06)',
                  overflow: 'hidden' 
                }" 
                to="/pools"
              >
                <div 
                  :style="{ background: mock.cardBg }" 
                  style="height: 200px; width: 100%;" 
                  class="relative-position flex flex-center"
                >
                  <div style="font-size: 4rem;">{{ mock.icon }}</div>
                  <div class="absolute-top-left q-ma-md">
                    <span class="custom-card-tag" :style="{ background: mock.tagBg, color: mock.tagColor }">
                      {{ getTagLabel(mock.tag) }}
                    </span>
                  </div>
                </div>

                <q-card-section class="q-pa-lg">
                  <!-- User Avatar Block -->
                  <div class="row items-center q-mb-md">
                    <q-avatar size="28px" :style="{ background: mock.avatarBg }" class="text-white text-weight-bold q-mr-sm" style="font-size: 0.75rem;">
                      {{ mock.ownerInitials }}
                    </q-avatar>
                    <span class="text-caption text-weight-medium" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'">{{ mock.ownerName }}</span>
                  </div>

                  <h3 class="text-subtitle1 text-weight-bold q-mb-sm line-clamp-2" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Inter', sans-serif; line-height: 1.35; height: 46px; margin-top:0;">
                    {{ mock.title }}
                  </h3>
                  <p class="text-caption line-clamp-2 q-mb-lg" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" style="height: 36px; line-height: 1.4;">
                    {{ mock.description }}
                  </p>

                  <!-- Progress Section -->
                  <div class="q-mb-md">
                    <div class="row justify-between items-center text-caption text-weight-bold q-mb-xs">
                       <span class="text-weight-bold" :class="$q.dark.isActive ? 'text-white' : 'text-dark'">{{ Math.round((mock.currentAmount / mock.goalAmount) * 100) }}%</span>
                       <span :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'">{{ mock.currentAmount }} MAD <span :class="$q.dark.isActive ? 'text-grey-6' : 'text-grey-5'" class="text-weight-regular">/ {{ mock.goalAmount }} MAD</span></span>
                    </div>
                    <q-linear-progress :value="mock.currentAmount / mock.goalAmount" style="height: 6px; border-radius: 99px;" color="primary" class="progress-bar-neon" />
                  </div>
                </q-card-section>

                <q-card-section class="q-px-lg q-pb-lg q-pt-none row items-center justify-between" :style="{ borderTop: $q.dark.isActive ? '1px solid rgba(255,255,255,0.04)' : '1px solid rgba(0,0,0,0.05)' }">
                  <div class="text-caption row items-center" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'">
                    <q-icon name="people" size="16px" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" class="q-mr-xs" />
                    {{ mock.contributors }} {{ $t('home.contributors') }}
                  </div>
                  <div class="text-caption row items-center" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'">
                    <q-icon name="schedule" size="16px" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" class="q-mr-xs" />
                    {{ mock.daysRemaining }}{{ $t('home.days') }} {{ $t('home.daysRemaining') }}
                  </div>
                </q-card-section>
              </q-card>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 5. COMMENT CA MARCHE – Horizontal high fidelity steps cards -->
    <div id="how-it-works" class="q-py-xxl" :style="{ background: $q.dark.isActive ? '#0A111E' : '#FFFFFF', padding: '100px 0' }">
      <div class="max-container q-px-xl">
        <div class="text-center q-mb-xxl">
          <h2 class="text-h3 text-weight-bold q-mb-sm" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Playfair Display', serif; letter-spacing: -0.5px; margin-top: 0;">
            {{ $t('home.stepsHeader') }}
          </h2>
        </div>

        <div class="q-mt-xl">
          <div class="row q-col-gutter-xl justify-center">
            <!-- Step 1 -->
            <div class="col-12 col-md-4">
              <div class="step-card q-pa-xl relative-position" :style="{
                background: $q.dark.isActive ? 'rgba(255, 255, 255, 0.02)' : '#FFFFFF',
                border: $q.dark.isActive ? '1px solid rgba(255, 255, 255, 0.06)' : '1px solid #f1f5f9',
                borderRadius: '20px',
                boxShadow: $q.dark.isActive ? 'none' : '0 4px 20px rgba(0,0,0,0.015)',
                minHeight: '230px'
              }">
                <div class="absolute" style="top: 28px; left: 36px;">
                  <span style="color: #16A34A; font-weight: 700; font-size: 1.1rem; border-bottom: 2.5px solid #16A34A; padding-bottom: 2px;">01</span>
                </div>
                
                <div class="row no-wrap items-start" style="margin-top: 28px;">
                  <div class="step-badge flex flex-center q-mr-lg" :style="{ background: $q.dark.isActive ? 'rgba(22, 163, 74, 0.12)' : '#E8F5E9' }" style="width: 64px; height: 64px; border-radius: 50%; flex-shrink: 0;">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#16A34A" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width: 28px; height: 28px;">
                      <path d="M12 20h9M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/>
                    </svg>
                  </div>
                  <div class="col">
                    <h3 class="text-weight-bold text-h6 q-mb-sm" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Inter', sans-serif; margin-top: 0;">
                      {{ $t('home.step1Title') }}
                    </h3>
                    <p :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'" style="font-size: 0.9rem; line-height: 1.6; margin: 0;">
                      {{ $t('home.step1Desc') }}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Step 2 -->
            <div class="col-12 col-md-4">
              <div class="step-card q-pa-xl relative-position" :style="{
                background: $q.dark.isActive ? 'rgba(255, 255, 255, 0.02)' : '#FFFFFF',
                border: $q.dark.isActive ? '1px solid rgba(255, 255, 255, 0.06)' : '1px solid #f1f5f9',
                borderRadius: '20px',
                boxShadow: $q.dark.isActive ? 'none' : '0 4px 20px rgba(0,0,0,0.015)',
                minHeight: '230px'
              }">
                <div class="absolute" style="top: 28px; left: 36px;">
                  <span style="color: #16A34A; font-weight: 700; font-size: 1.1rem; border-bottom: 2.5px solid #16A34A; padding-bottom: 2px;">02</span>
                </div>
                
                <div class="row no-wrap items-start" style="margin-top: 28px;">
                  <div class="step-badge flex flex-center q-mr-lg" :style="{ background: $q.dark.isActive ? 'rgba(22, 163, 74, 0.12)' : '#E8F5E9' }" style="width: 64px; height: 64px; border-radius: 50%; flex-shrink: 0;">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#16A34A" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width: 28px; height: 28px;">
                      <circle cx="18" cy="5" r="3" />
                      <circle cx="6" cy="12" r="3" />
                      <circle cx="18" cy="19" r="3" />
                      <line x1="8.59" y1="13.51" x2="15.42" y2="17.49" />
                      <line x1="15.41" y1="6.51" x2="8.59" y2="10.49" />
                    </svg>
                  </div>
                  <div class="col">
                    <h3 class="text-weight-bold text-h6 q-mb-sm" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Inter', sans-serif; margin-top: 0;">
                      {{ $t('home.step2Title') }}
                    </h3>
                    <p :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'" style="font-size: 0.9rem; line-height: 1.6; margin: 0;">
                      {{ $t('home.step2Desc') }}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Step 3 -->
            <div class="col-12 col-md-4">
              <div class="step-card q-pa-xl relative-position" :style="{
                background: $q.dark.isActive ? 'rgba(255, 255, 255, 0.02)' : '#FFFFFF',
                border: $q.dark.isActive ? '1px solid rgba(255, 255, 255, 0.06)' : '1px solid #f1f5f9',
                borderRadius: '20px',
                boxShadow: $q.dark.isActive ? 'none' : '0 4px 20px rgba(0,0,0,0.015)',
                minHeight: '230px'
              }">
                <div class="absolute" style="top: 28px; left: 36px;">
                  <span style="color: #16A34A; font-weight: 700; font-size: 1.1rem; border-bottom: 2.5px solid #16A34A; padding-bottom: 2px;">03</span>
                </div>
                
                <div class="row no-wrap items-start" style="margin-top: 28px;">
                  <div class="step-badge flex flex-center q-mr-lg" :style="{ background: $q.dark.isActive ? 'rgba(22, 163, 74, 0.12)' : '#E8F5E9' }" style="width: 64px; height: 64px; border-radius: 50%; flex-shrink: 0;">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#16A34A" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width: 28px; height: 28px;">
                      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                      <path d="m9 11 2 2 4-4" />
                    </svg>
                  </div>
                  <div class="col">
                    <h3 class="text-weight-bold text-h6 q-mb-sm" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Inter', sans-serif; margin-top: 0;">
                      {{ $t('home.step3Title') }}
                    </h3>
                    <p :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-7'" style="font-size: 0.9rem; line-height: 1.6; margin: 0;">
                      {{ $t('home.step3Desc') }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 6. TOUT CE QU'IL FAUT – Features Grid Section -->
    <div class="q-py-xxl" :style="{ background: $q.dark.isActive ? '#090E17' : '#F8FAFC', padding: '100px 0' }">
      <div class="max-container q-px-xl">
        <div class="text-center q-mb-xxl" style="margin-bottom: 64px;">
          <div class="text-overline text-weight-bold q-mb-xs" style="letter-spacing: 2px; color: #16A34A !important;">
            {{ $t('home.whyPotify') }}
          </div>
          <h2 class="text-h3 text-weight-bold q-mb-sm" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Playfair Display', serif; font-style: italic; letter-spacing: -0.5px;">
            {{ $t('home.whyPotifyTitle') }}
          </h2>
          <p :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-7'" style="font-size: 1.05rem; font-weight: 300;">
            {{ $t('home.whyPotifySubtitle') }}
          </p>
        </div>

        <div class="row q-col-gutter-lg">
          <!-- Feature 1 -->
          <div class="col-12 col-sm-6 col-md-4">
            <q-card class="feature-card flat shadow-0 q-pa-xl" :style="{ borderRadius: '24px', border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(22,163,74,0.05)', background: $q.dark.isActive ? 'rgba(255,255,255,0.03)' : 'white' }">
              <div class="feature-icon-circle flex flex-center q-mb-md" style="background: #F3E8FF; color: #7C3AED;">
                <q-icon name="smart_toy" size="24px" />
              </div>
              <h3 class="text-subtitle1 text-weight-bold q-mb-xs" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="margin-top:0;">{{ $t('home.feature1Title') }}</h3>
              <p :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" style="font-size: 0.9rem; line-height: 1.6; margin-bottom:0;">
                {{ $t('home.feature1Desc') }}
              </p>
            </q-card>
          </div>

          <!-- Feature 2 -->
          <div class="col-12 col-sm-6 col-md-4">
            <q-card class="feature-card flat shadow-0 q-pa-xl" :style="{ borderRadius: '24px', border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(22,163,74,0.05)', background: $q.dark.isActive ? 'rgba(255,255,255,0.03)' : 'white' }">
              <div class="feature-icon-circle flex flex-center q-mb-md" style="background: #E0F2FE; color: #0284C7;">
                <q-icon name="shield" size="24px" />
              </div>
              <h3 class="text-subtitle1 text-weight-bold q-mb-xs" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="margin-top:0;">{{ $t('home.feature2Title') }}</h3>
              <p :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" style="font-size: 0.9rem; line-height: 1.6; margin-bottom:0;">
                {{ $t('home.feature2Desc') }}
              </p>
            </q-card>
          </div>

          <!-- Feature 3 -->
          <div class="col-12 col-sm-6 col-md-4">
            <q-card class="feature-card flat shadow-0 q-pa-xl" :style="{ borderRadius: '24px', border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(22,163,74,0.05)', background: $q.dark.isActive ? 'rgba(255,255,255,0.03)' : 'white' }">
              <div class="feature-icon-circle flex flex-center q-mb-md" style="background: #DCFCE7; color: #16A34A;">
                <q-icon name="verified_user" size="24px" />
              </div>
              <h3 class="text-subtitle1 text-weight-bold q-mb-xs" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="margin-top:0;">{{ $t('home.feature3Title') }}</h3>
              <p :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'" style="font-size: 0.9rem; line-height: 1.6; margin-bottom:0;">
                {{ $t('home.feature3Desc') }}
              </p>
            </q-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 7. COMMENTAIRES DES UTILISATEURS – Testimonials Section -->
    <div class="q-py-xxl" :style="{ background: $q.dark.isActive ? '#0A1120' : '#FFFFFF', padding: '100px 0' }">
      <div class="max-container q-px-xl">
        <div class="text-center q-mb-xxl" style="margin-bottom: 64px;">
          <div class="text-overline text-weight-bold q-mb-xs" style="letter-spacing: 2px; color: #16A34A !important;">
            {{ $t('home.testimonialHeader') }}
          </div>
          <h2 class="text-h3 text-weight-bold q-mb-sm" :class="$q.dark.isActive ? 'text-white' : 'text-dark'" style="font-family: 'Playfair Display', serif; font-style: italic; letter-spacing: -0.5px;">
            {{ $t('home.testimonialTitle') }}
          </h2>
          <p :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-7'" style="font-size: 1.05rem; font-weight: 300;">
            {{ $t('home.testimonialSubtitle') }}
          </p>
        </div>

        <div class="row q-col-gutter-lg">
          <!-- Testimonial 1 -->
          <div class="col-12 col-md-4">
            <q-card class="testimonial-card flat shadow-0 q-pa-xl" :style="{ borderRadius: '24px', background: $q.dark.isActive ? 'rgba(255,255,255,0.03)' : '#F8FAFC', border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(22,163,74,0.04)', height: '100%' }">
              <div class="q-mb-md">
                <q-icon name="star" size="18px" color="amber-7" v-for="i in 5" :key="i" />
              </div>
              <p class="text-body2 q-mb-lg" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-8'" style="font-style: italic; line-height: 1.7; font-size: 0.95rem;">
                {{ $t('home.testimonial1Text') }}
              </p>
              <div class="row items-center">
                <q-avatar size="36px" color="teal-6" class="text-white text-weight-bold q-mr-sm" style="font-size: 0.95rem;">
                  SK
                </q-avatar>
                <div>
                  <div class="text-subtitle2 text-weight-bold" :class="$q.dark.isActive ? 'text-white' : 'text-dark'">Sarah K. (Casablanca)</div>
                  <div class="text-caption" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'">{{ $t('home.testimonial1Role') }}</div>
                </div>
              </div>
            </q-card>
          </div>

          <!-- Testimonial 2 -->
          <div class="col-12 col-md-4">
            <q-card class="testimonial-card flat shadow-0 q-pa-xl" :style="{ borderRadius: '24px', background: $q.dark.isActive ? 'rgba(255,255,255,0.03)' : '#F8FAFC', border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(22,163,74,0.04)', height: '100%' }">
              <div class="q-mb-md">
                <q-icon name="star" size="18px" color="amber-7" v-for="i in 5" :key="i" />
              </div>
              <p class="text-body2 q-mb-lg" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-8'" style="font-style: italic; line-height: 1.7; font-size: 0.95rem;">
                {{ $t('home.testimonial2Text') }}
              </p>
              <div class="row items-center">
                <q-avatar size="36px" color="purple-6" class="text-white text-weight-bold q-mr-sm" style="font-size: 0.95rem;">
                  KA
                </q-avatar>
                <div>
                  <div class="text-subtitle2 text-weight-bold" :class="$q.dark.isActive ? 'text-white' : 'text-dark'">Karim A. (Rabat)</div>
                  <div class="text-caption" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'">{{ $t('home.testimonial2Role') }}</div>
                </div>
              </div>
            </q-card>
          </div>

          <!-- Testimonial 3 -->
          <div class="col-12 col-md-4">
            <q-card class="testimonial-card flat shadow-0 q-pa-xl" :style="{ borderRadius: '24px', background: $q.dark.isActive ? 'rgba(255,255,255,0.03)' : '#F8FAFC', border: $q.dark.isActive ? '1px solid rgba(255,255,255,0.06)' : '1px solid rgba(22,163,74,0.04)', height: '100%' }">
              <div class="q-mb-md">
                <q-icon name="star" size="18px" color="amber-7" v-for="i in 5" :key="i" />
              </div>
              <p class="text-body2 q-mb-lg" :class="$q.dark.isActive ? 'text-grey-4' : 'text-grey-8'" style="font-style: italic; line-height: 1.7; font-size: 0.95rem;">
                {{ $t('home.testimonial3Text') }}
              </p>
              <div class="row items-center">
                <q-avatar size="36px" color="blue-6" class="text-white text-weight-bold q-mr-sm" style="font-size: 0.95rem;">
                  MD
                </q-avatar>
                <div>
                  <div class="text-subtitle2 text-weight-bold" :class="$q.dark.isActive ? 'text-white' : 'text-dark'">Marc D. (Marrakech)</div>
                  <div class="text-caption" :class="$q.dark.isActive ? 'text-grey-5' : 'text-grey-6'">{{ $t('home.testimonial3Role') }}</div>
                </div>
              </div>
            </q-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 8. FINAL CALL TO ACTION – Clean Minimal Banner -->
    <div class="q-py-xxl text-white text-center relative-position overflow-hidden" 
         :style="{ 
           background: $q.dark.isActive ? 'radial-gradient(circle at center, #15803d 0%, #0A111E 100%)' : 'radial-gradient(circle at center, #166534 0%, #062f25 100%)', 
           padding: '120px 0' 
         }">
      <div class="max-container q-px-xl" style="max-width: 650px;">
        <h2 class="text-h3 text-weight-bold q-mb-md" style="font-family: 'Playfair Display', serif; font-style: italic; letter-spacing: -0.5px; margin-top:0; line-height: 1.2;">
          {{ $t('home.ctaTitle') }}
        </h2>
        <p class="text-body2 q-mb-xl text-grey-4" style="line-height: 1.7; font-size: 1.05rem; font-weight: 300; max-width: 540px; margin-left: auto; margin-right: auto;">
          {{ $t('home.ctaSubtitle') }}
        </p>
        <q-btn
          no-caps
          unelevated
          class="btn-hero-primary text-white"
          :label="$t('home.ctaButton')"
          style="padding: 16px 48px; border-radius: 12px; font-weight: 700; font-size: 1rem; box-shadow: 0 4px 18px rgba(22, 163, 74, 0.25);"
          to="/pools/create"
        />
      </div>
    </div>

  </q-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { poolApi } from 'boot/axios'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t, locale } = useI18n()

const getCategoryLabel = (name) => {
  const keyMap = {
    'Santé': 'catHealth',
    'Solidarité': 'catSolidarity',
    'Éducation': 'catEducation',
    'Entrepreneuriat': 'catEntrepreneurship',
    'Événements': 'catEvents',
    'Associations': 'catAssociations',
    'Urgence': 'catEmergency',
    'Autres': 'catOthers'
  }
  const key = keyMap[name]
  return key ? t(`home.${key}`) : name
}

const getTagLabel = (tag) => {
  if (!tag || tag === 'Actif') return t('publicPools.statusActive') || 'Actif'
  const lang = locale.value
  const tags = {
    'Urgent': { fr: 'Urgent', en: 'Urgent', es: 'Urgente', ar: 'عاجل' },
    'Tendance': { fr: 'Tendance', en: 'Trending', es: 'Tendencia', ar: 'شائع' },
    'Nouveau': { fr: 'Nouveau', en: 'New', es: 'Nuevo', ar: 'جديد' }
  }
  return tags[tag]?.[lang] || tag
}

const scrollToHowItWorks = () => {
  const el = document.getElementById('how-it-works')
  if (el) el.scrollIntoView({ behavior: 'smooth' })
}

const pools = ref([])

const collectedCount = ref(0)
const poolsCount = ref(0)
const contributorsCount = ref(0)

const animateCounters = () => {
  const duration = 1800
  const startTime = performance.now()
  
  const step = (now) => {
    const progress = Math.min((now - startTime) / duration, 1)
    const ease = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress) // outExpo easing
    
    collectedCount.value = Math.round(ease * 12)
    poolsCount.value = Math.round(ease * 15000)
    contributorsCount.value = (ease * 2.5).toFixed(1)
    
    if (progress < 1) {
      requestAnimationFrame(step)
    } else {
      collectedCount.value = 12
      poolsCount.value = 15000
      contributorsCount.value = 2.5
    }
  }
  requestAnimationFrame(step)
}

const mockPools = computed(() => [
  {
    id: 'mock-1',
    title: t("home.mock1Title"),
    description: t("home.mock1Desc"),
    category: 'Santé',
    currentAmount: 44000,
    goalAmount: 50000,
    icon: '🩺',
    ownerName: "Fatima Z.",
    ownerInitials: "FZ",
    avatarBg: "#EF4444",
    tag: "Urgent",
    tagBg: "#FEE2E2",
    tagColor: "#EF4444",
    contributors: 312,
    daysRemaining: 8,
    cardBg: "#FFE4E6"
  },
  {
    id: 'mock-2',
    title: t("home.mock2Title"),
    description: t("home.mock2Desc"),
    category: 'Éducation',
    currentAmount: 31500,
    goalAmount: 50000,
    icon: '🎓',
    ownerName: "Youssef B.",
    ownerInitials: "YB",
    avatarBg: "#8B5CF6",
    tag: "Tendance",
    tagBg: "#FEF3C7",
    tagColor: "#D97706",
    contributors: 189,
    daysRemaining: 21,
    cardBg: "#F3E8FF"
  },
  {
    id: 'mock-3',
    title: t("home.mock3Title"),
    description: t("home.mock3Desc"),
    category: 'Autres',
    currentAmount: 8500,
    goalAmount: 25000,
    icon: '🌱',
    ownerName: "Nadia M.",
    ownerInitials: "NM",
    avatarBg: "#10B981",
    tag: "Nouveau",
    tagBg: "#DCFCE7",
    tagColor: "#16A34A",
    contributors: 67,
    daysRemaining: 45,
    cardBg: "#DCFCE7"
  }
])

const categories = [
  {
    name: 'Santé',
    svg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" /><path d="M7.5 12h2.5l1.5-3 2 6 1.5-3h2" stroke-width="1.8" /></svg>',
    color: '#E8F5E9',
    iconColor: '#10B981',
    countNum: 842,
    countText: 'projets'
  },
  {
    name: 'Solidarité',
    svg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 13c1-1.5 2-2 3-2 1.5 0 2.5 1 2.5 2.5 0 2-2.5 4-5.5 6-3-2-5.5-4-5.5-6C6.5 12 7.5 11 9 11c1 0 2 .5 3 2z" /><path d="M2 14c2-1 4.5 0 6 2M16 16c1.5-2 4-3 6-2" /></svg>',
    color: '#FEF3C7',
    iconColor: '#D97706',
    countNum: 634,
    countText: 'projets'
  },
  {
    name: 'Éducation',
    svg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z" /><path d="M6 12v5c0 2 2.68 3 6 3s6-1 6-3v-5" /></svg>',
    color: '#F3E8FF',
    iconColor: '#7C3AED',
    countNum: 521,
    countText: 'projets'
  },
  {
    name: 'Entrepreneuriat',
    svg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M4.5 16.5c-1.5 1.5-2.5 3.5-2.5 5.5C4 22 6 21 7.5 19.5" /><path d="M12 12l9-9-3 12-6 3-3-3-3-6 6-3 3 3z" /><path d="M9 15l-3 3M15 9l3-3" /></svg>',
    color: '#E6F4EA',
    iconColor: '#16A34A',
    countNum: 389,
    countText: 'projets'
  },
  {
    name: 'Événements',
    svg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 10L6 22M14 6L2 18M18 10l-4-4L2 18l4 4 12-12z" /><path d="M19 7l3-3M15 3l1-2M21 9l2 1M18 2l2 2" stroke-width="1.5" /></svg>',
    color: '#FFEDD5',
    iconColor: '#EA580C',
    countNum: 276,
    countText: 'projets'
  },
  {
    name: 'Associations',
    svg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 10L12 3l10 7H2z" /><path d="M4 10v10M9 10v10M15 10v10M20 10v10" /><path d="M2 20h20M2 22h20" /></svg>',
    color: '#E0F2FE',
    iconColor: '#0284C7',
    countNum: 198,
    countText: 'projets'
  },
  {
    name: 'Urgence',
    svg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 18a7 7 0 0 1 14 0H5z" /><rect x="3" y="18" width="18" height="3" rx="1" /><path d="M12 2v3M4.22 5.22l2.12 2.12M19.78 5.22l-2.12 2.12" stroke-width="1.5" /></svg>',
    color: '#FEE2E2',
    iconColor: '#DC2626',
    countNum: 94,
    countText: 'projets'
  },
  {
    name: 'Autres',
    svg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c0 4.5 1.5 6 6 6-4.5 0-6 1.5-6 6 0-4.5-1.5-6-6-6 4.5 0 6-1.5 6-6z" /><path d="M19 15c0 2 1 2.5 3 2.5-2 0-3 .5-3 2.5 0-2-1-2.5-3-2.5 2 0 3-.5 3-2.5z" stroke-width="1.5" /></svg>',
    color: '#F1F5F9',
    iconColor: '#475569',
    countNum: 286,
    countText: 'projets'
  }
]



const fetchPools = async () => {
  try {
    const response = await poolApi.get('/pools')
    const list = (response.data || []).filter(p => !p.parentId).slice(0, 3)
    pools.value = list.map((p, idx) => {
      // Map icons based on category
      let icon = '✨'
      let cardBg = '#F1F5F9'
      let tagBg = '#E2E8F0'
      let tagColor = '#475569'
      let tag = 'Actif'
      let avatarBg = '#EF4444'

      if (p.category === 'Santé') { 
        icon = '🩺'
        cardBg = '#FFE4E6'
        tagBg = '#FEE2E2'
        tagColor = '#EF4444'
        tag = 'Urgent'
        avatarBg = '#EF4444'
      } else if (p.category === 'Éducation') { 
        icon = '🎓'
        cardBg = '#F3E8FF'
        tagBg = '#FEF3C7'
        tagColor = '#D97706'
        tag = 'Tendance'
        avatarBg = '#8B5CF6'
      } else if (p.category === 'Urgence') { 
        icon = '🚨'
        cardBg = '#FEE2E2'
        tagBg = '#FEE2E2'
        tagColor = '#DC2626'
        tag = 'Urgent'
        avatarBg = '#DC2626'
      } else if (p.category === 'Solidarité') { 
        icon = '🤝'
        cardBg = '#FEF3C7'
        tagBg = '#FEF3C7'
        tagColor = '#D97706'
        tag = 'Tendance'
        avatarBg = '#F59E0B'
      } else if (p.category === 'Entrepreneuriat') { 
        icon = '🚀'
        cardBg = '#DCFCE7'
        tagBg = '#DCFCE7'
        tagColor = '#16A34A'
        tag = 'Nouveau'
        avatarBg = '#10B981'
      }
      
      const ownerName = p.creatorName || p.ownerName || t('home.memberPotify')
      const initials = ownerName.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
      
      return {
        ...p,
        icon,
        cardBg,
        ownerName,
        ownerInitials: initials || 'P',
        avatarBg,
        tag,
        tagBg,
        tagColor,
        contributors: p.contributorsCount || Math.floor(p.currentAmount / 150) + 3,
        daysRemaining: p.daysRemaining || Math.max(5, 30 - idx * 7)
      }
    })
  } catch (error) {
    console.error('Erreur lors du chargement des cagnottes:', error)
  }
}

const contribute = (pool) => {
  router.push(`/pools/${pool.id}`)
}

const getProgressValue = (pool) => {
  if (!pool.goalAmount) return 0
  return Math.min(pool.currentAmount / pool.goalAmount, 1)
}

const getPercentageText = (pool) => {
  if (!pool.goalAmount) return '0%'
  const percentage = Math.round((pool.currentAmount / pool.goalAmount) * 100)
  return `${percentage}%`
}

onMounted(() => {
  fetchPools()
  animateCounters()
})
</script>

<style scoped>
.max-container {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

/* Premium Badge */
.badge-premium {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
  box-shadow: 0 1px 3px rgba(22, 163, 74, 0.02) !important;
}
.badge-premium:hover {
  transform: translateY(-1px);
  background: rgba(22, 163, 74, 0.1) !important;
  border-color: rgba(22, 163, 74, 0.35) !important;
  box-shadow: 0 4px 12px rgba(22, 163, 74, 0.06) !important;
}

/* Floating animation for Hero image */
@keyframes floatImage {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0px); }
}
.hero-floating-image {
  animation: floatImage 8s ease-in-out infinite;
}

/* Fade up animation for Hero content */
@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.fade-up-text {
  animation: fadeUp 1.2s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

/* Button transitions */
.btn-hero-primary {
  background: #16A34A !important;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1) !important;
}
.btn-hero-primary:hover {
  background: #15803d !important;
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 24px rgba(22, 163, 74, 0.25) !important;
}

.btn-hero-secondary {
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1) !important;
}
.btn-hero-secondary:hover {
  background: rgba(22, 163, 74, 0.04) !important;
  border-color: #16A34A !important;
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04) !important;
}

/* Simple Badge */
.simple-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 99px;
  border: 1px solid rgba(0,0,0,0.08);
}
.badge-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  margin-right: 8px;
}
.badge-text {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Dark Category Card Styling */
.category-dark-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.06) !important;
  border-color: #16A34A !important;
  box-shadow: 0 10px 25px rgba(22, 163, 74, 0.12);
}
.category-dark-card:hover .arrow-icon {
  transform: translateX(4px);
  color: #16A34A !important;
}

/* Premium Pool Card Styling */
.premium-pool-card {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.premium-pool-card:hover {
  transform: translateY(-8px);
  border-color: #16A34A !important;
  box-shadow: 0 15px 30px rgba(22, 163, 74, 0.1) !important;
}
.custom-card-tag {
  font-size: 0.65rem;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 99px;
  text-transform: uppercase;
}
.progress-bar-neon {
  background: rgba(255,255,255,0.08);
}

/* Steps Connection Line */
.steps-container {
  z-index: 1;
}
.steps-connecting-line {
  position: absolute;
  top: 32px;
  left: 12.5%;
  right: 12.5%;
  height: 2px;
  background: #E2E8F0;
  z-index: -1;
}
.step-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #16A34A 0%, #4ade80 100%);
  box-shadow: 0 8px 20px rgba(22, 163, 74, 0.15);
}
.step-number-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #16A34A;
  color: white;
  font-size: 0.75rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid white;
}

/* Feature Card Styling */
.feature-card {
  transition: all 0.3s ease;
}
.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.04) !important;
  border-color: rgba(22, 163, 74, 0.2) !important;
}
.feature-icon-circle {
  width: 48px;
  height: 48px;
  border-radius: 12px;
}

/* Testimonial Card Styling */
.testimonial-card {
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
}
.testimonial-card:hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow: 0 16px 32px rgba(22, 163, 74, 0.05) !important;
  border-color: rgba(22, 163, 74, 0.2) !important;
}

/* Category Card Refinement */
.category-dark-card {
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
}
.category-dark-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 16px 32px rgba(22, 163, 74, 0.08) !important;
  border-color: rgba(22, 163, 74, 0.35) !important;
}
.category-dark-card:hover .category-arrow-btn {
  background: #16A34A !important;
  border-color: #16A34A !important;
  transform: scale(1.05);
}
.category-dark-card:hover .category-arrow-btn .q-icon {
  color: #FFFFFF !important;
}

/* Feature Card Refinement */
.feature-card {
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1) !important;
}
.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px rgba(22, 163, 74, 0.06) !important;
  border-color: rgba(22, 163, 74, 0.25) !important;
}

/* Linear progress bar styling override */
.progress-bar-neon {
  background: rgba(22, 163, 74, 0.08) !important;
}
.progress-bar-neon .q-linear-progress__model {
  background: #16A34A !important;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
