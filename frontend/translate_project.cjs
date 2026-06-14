const fs = require('fs');
const path = require('path');

// 1. Replacements for Vue components
const replacements = {
  // 1. User Dashboard Page
  'src/pool/pages/UserDashboardPage.vue': [
    ['<h1 class="text-h4 text-weight-bold q-my-none" style="color: #0D1B2E;">Mon Espace</h1>', '<h1 class="text-h4 text-weight-bold q-my-none" style="color: #0D1B2E;">{{ $t(\'dashboard.mySpace\') }}</h1>'],
    ['<p class="text-subtitle1 text-grey-7">Bienvenue, {{ userName }}. Gérez vos cagnottes et suivez vos contributions.</p>', '<p class="text-subtitle1 text-grey-7">{{ $t(\'dashboard.welcome\', { name: userName }) }}</p>'],
    ['label="Mes Cagnottes"', ':label="$t(\'dashboard.myPools\')"'],
    ['label="Partagées avec moi"', ':label="$t(\'dashboard.sharedWithMe\')"'],
    ['label="Mes Contributions"', ':label="$t(\'dashboard.myContributions\')"'],
    ['label="Mes Statistiques"', ':label="$t(\'dashboard.myStats\')"'],
    ['label="Mes Notifications"', ':label="$t(\'dashboard.myNotifications\')"'],
    ['label="Modifier" @click.stop="$router.push(`/pools/${pool.id}/edit`)"', ':label="$t(\'dashboard.edit\')" @click.stop="$router.push(`/pools/${pool.id}/edit`)"'],
    ['label="Voir" @click.stop="$router.push(`/pools/${pool.id}`)"', ':label="$t(\'dashboard.view\')" @click.stop="$router.push(`/pools/${pool.id}`)"'],
    ['Vous n\'avez pas encore créé de cagnotte', '{{ $t(\'dashboard.noPoolsCreated\') }}'],
    ['label="Lancer ma première cagnotte"', ':label="$t(\'dashboard.launchFirstPool\')"'],
    ['Organisé par', '{{ $t(\'dashboard.organizedBy\') }}'],
    ['Invité', '{{ $t(\'dashboard.invited\') }}'],
    ['Aucune cagnotte partagée avec vous', '{{ $t(\'dashboard.noPoolsShared\') }}'],
    ['Lorsqu\'un ami vous invite à sa cagnotte, elle apparaîtra ici.', '{{ $t(\'dashboard.sharedInvitationDesc\') }}'],
    ['Don de {{ contrib.amount }} € pour <span class="text-primary">{{ getPoolTitle(contrib.poolId) }}</span>', '{{ $t(\'dashboard.donationOf\', { amount: contrib.amount }) }} {{ $t(\'dashboard.for\') }} <span class="text-primary">{{ getPoolTitle(contrib.poolId) }}</span>'],
    ['Catégorie : <strong>{{ getPoolCategory(contrib.poolId) }}</strong>', '{{ $t(\'dashboard.category\') }} : <strong>{{ getPoolCategory(contrib.poolId) }}</strong>'],
    ['Statut : <span :class="(contrib.status === \'REUSSIE\' || contrib.status === \'CONFIRMED\') ? \'text-green text-weight-bold\' : \'text-orange\'">{{ contrib.status === \'CONFIRMED\' ? \'CONFIRMÉ\' : contrib.status }}</span>', '{{ $t(\'dashboard.status\') }} : <span :class="(contrib.status === \'REUSSIE\' || contrib.status === \'CONFIRMED\') ? \'text-green text-weight-bold\' : \'text-orange\'">{{ contrib.status === \'CONFIRMED\' ? $t(\'dashboard.confirmed\') : contrib.status }}</span>'],
    ['label="Reçu PDF"', ':label="$t(\'dashboard.pdfReceipt\')"'],
    ['label="Détails" size="sm" :to="`/pools/${contrib.poolId}`"', ':label="$t(\'dashboard.details\')" size="sm" :to="`/pools/${contrib.poolId}`"'],
    ['Vous n\'avez pas encore fait de don', '{{ $t(\'dashboard.noDonationsMade\') }}'],
    ['label="Découvrir les cagnottes"', ':label="$t(\'dashboard.discoverPools\')"'],
    ['Total Récolté (Organisateur)', '{{ $t(\'dashboard.totalCollected\') }}'],
    ['cagnottes créées', '{{ $t(\'dashboard.poolsCreated\') }}'],
    ['Contributeurs Uniques', '{{ $t(\'dashboard.uniqueContributors\') }}'],
    ['Sur vos cagnottes actives', '{{ $t(\'dashboard.onActivePools\') }}'],
    ['Mes Dons Totaux (Donateur)', '{{ $t(\'dashboard.myTotalDonations\') }}'],
    ['dons effectués', '{{ $t(\'dashboard.donationsMade\') }}'],
    ['Progression cumulative des fonds récoltés', '{{ $t(\'dashboard.cumulativeProgression\') }}'],
    ['Aucun don reçu pour le moment pour tracer la courbe de progression.', '{{ $t(\'dashboard.noDonationsToChart\') }}'],
    ['Mes dons par catégorie', '{{ $t(\'dashboard.myDonationsByCategory\') }}'],
    ['Faites des dons pour voir la répartition par catégorie.', '{{ $t(\'dashboard.makeDonationsToChart\') }}'],
    ['Répartition des dons par tranches de montants', '{{ $t(\'dashboard.donationsByBracket\') }}'],
    ['Aucun don reçu pour le moment.', '{{ $t(\'dashboard.noDonationReceivedYet\') }}'],
    ['Dernières notifications reçues', '{{ $t(\'dashboard.latestNotifications\') }}'],
    ['label="Tout marquer comme lu"', ':label="$t(\'dashboard.markAllAsRead\')"'],
    ['Aucune notification pour le moment', '{{ $t(\'dashboard.noNotificationsYet\') }}'],
    ['Vous recevrez des alertes ici lors de nouvelles activités.', '{{ $t(\'dashboard.noNotificationsDesc\') }}'],
    ['Marquer comme lu', '{{ $t(\'nav.markRead\') }}']
  ],

  // 2. Create Pool Page
  'src/pool/pages/CreatePoolPage.vue': [
    ['Créer une nouvelle cagnotte', '{{ $t(\'createPool.title\') }}'],
    ['Donnez vie à votre projet solidaire en quelques étapes.', '{{ $t(\'createPool.subtitle\') }}'],
    ['Je suis PotiBuddy, votre assistant intelligent. Décrivez votre projet de cagnotte en quelques mots, et je vous aiderai à le concevoir, le structurer et remplir le formulaire automatiquement (titre, budget, description et phases de financement) !', '{{ $t(\'createPool.aiDesc\') }}'],
    ['label="Générer avec l\'IA"', ':label="$t(\'createPool.aiButton\')"'],
    ['Suggestion générée :', '{{ $t(\'createPool.aiSuggestion\') }} :'],
    ['Titre suggéré :', '{{ $t(\'createPool.aiSuggestedTitle\') }} :'],
    ['Budget total :', '{{ $t(\'createPool.aiSuggestedBudget\') }} :'],
    ['Description :', '{{ $t(\'createPool.aiSuggestedDesc\') }} :'],
    ['STRUCTURE ET PHASES SUGGÉRÉES :', '{{ $t(\'createPool.aiSuggestedStructure\') }} :'],
    ['Cagnotte Simple avec Phases :', '{{ $t(\'createPool.aiSuggestedSimple\') }} :'],
    ['Cagnotte Multi-sous-cagnottes :', '{{ $t(\'createPool.aiSuggestedMulti\') }} :'],
    ['label="Ignorer"', ':label="$t(\'createPool.aiIgnore\')"'],
    ['label="Appliquer le projet "', ':label="$t(\'createPool.aiApply\')"'],
    ['1. Structure de la cagnotte', '{{ $t(\'createPool.step1Header\') }}'],
    ['Cagnotte Simple', '{{ $t(\'createPool.simplePool\') }}'],
    ['Une seule étape de financement', '{{ $t(\'createPool.simplePoolDesc\') }}'],
    ['Cagnotte Complexe (Multi-Sous-Cagnottes)', '{{ $t(\'createPool.complexPool\') }}'],
    ['Divisée en plusieurs sous-cagnottes et phases', '{{ $t(\'createPool.complexPoolDesc\') }}'],
    ['2. Détails généraux de la cagnotte', '{{ $t(\'createPool.step2Header\') }}'],
    ['label="Titre de la cagnotte principale *"', ':label="$t(\'createPool.mainTitleLabel\')"'],
    ['Le titre est obligatoire', '{{ $t(\'createPool.titleRequired\') }}'],
    ['label="Description *"', ':label="$t(\'createPool.descriptionLabel\')"'],
    ['La description est obligatoire', '{{ $t(\'createPool.descriptionRequired\') }}'],
    ['label="Catégorie"', ':label="$t(\'createPool.categoryLabel\')"'],
    ['label="Objectif financier total (€) *"', ':label="$t(\'createPool.goalAmountLabel\')"'],
    ['Le montant doit être supérieur à 0', '{{ $t(\'createPool.amountPositive\') }}'],
    ['Calculé automatiquement à partir des phases', '{{ $t(\'createPool.calculatedFromPhasesHint\') }}'],
    ['Date limite pour la cagnotte principale', '{{ $t(\'createPool.hasDeadlineToggle\') }}'],
    ['label="Date de fin *"', ':label="$t(\'createPool.deadlineLabel\')"'],
    ['La date est obligatoire', '{{ $t(\'createPool.deadlineRequired\') }}'],
    ['3. Visibilité & Type de cagnotte', '{{ $t(\'createPool.step3Header\') }}'],
    ['label="Type de visibilité"', ':label="$t(\'createPool.typeLabel\')"'],
    ['Cagnotte Publique (Visible par tous)', '{{ $t(\'createPool.typePublic\') }}'],
    ['Cagnotte Privée (Accessible uniquement par invitation / lien)', '{{ $t(\'createPool.typePrivate\') }}'],
    ['Cagnotte Privée avec option Tontine (Membres invités uniquement, contributions partagées)', '{{ $t(\'createPool.typeTontine\') }}'],
    ['Média de présentation (Optionnel)', '{{ $t(\'createPool.mediaHeader\') }}'],
    ['label="Image illustrative"', ':label="$t(\'createPool.imageLabel\')"'],
    ['Glissez-déposez ou cliquez pour choisir une image', '{{ $t(\'createPool.imageUploadHint\') }}'],
    ['label="Lien Vidéo YouTube ou Vimeo"', ':label="$t(\'createPool.videoUrlLabel\')"'],
    ['4. Phases de financement (cagnotte simple)', '{{ $t(\'createPool.phasesHeaderSimple\') }}'],
    ['Configurez un ou plusieurs objectifs successifs.', '{{ $t(\'createPool.phasesSubSimple\') }}'],
    ['label="Ajouter une phase de financement"', ':label="$t(\'createPool.addPhaseButton\')"'],
    ['label="Titre de la phase *"', ':label="$t(\'createPool.phaseTitleLabel\')"'],
    ['label="Objectif financier de la phase (€) *"', ':label="$t(\'createPool.phaseGoalLabel\')"'],
    ['Calculé automatiquement à partir de la somme des phases', '{{ $t(\'createPool.sumPhasesHint\') }}'],
    ['4. Sous-cagnottes & Phases (cagnotte complexe)', '{{ $t(\'createPool.phasesHeaderComplex\') }}'],
    ['Configurez plusieurs projets indépendants.', '{{ $t(\'createPool.phasesSubComplex\') }}'],
    ['label="Ajouter une sous-cagnotte"', ':label="$t(\'createPool.addSubPoolButton\')"'],
    ['label="Titre de la sous-cagnotte *"', ':label="$t(\'createPool.subPoolTitleLabel\')"'],
    ['label="Description de la sous-cagnotte"', ':label="$t(\'createPool.subPoolDescLabel\')"'],
    ['Date limite pour cette sous-cagnotte', '{{ $t(\'createPool.subPoolDeadlineToggle\') }}'],
    ['Phases de financement de cette sous-cagnotte', '{{ $t(\'createPool.subPoolPhasesHeader\') }}'],
    ['label="Ajouter une phase à la sous-cagnotte"', ':label="$t(\'createPool.addSubPoolPhaseButton\')"'],
    ['label="Annuler"', ':label="$t(\'createPool.cancel\')"'],
    ['label="Créer le projet solidaire"', ':label="$t(\'createPool.create\')"'],
    ['Cagnotte créée avec succès !', '{{ $t(\'createPool.success\') }}'],
    ['Erreur lors de la création de la cagnotte', '{{ $t(\'createPool.error\') }}']
  ],

  // 3. Public Pools Page
  'src/pool/pages/PublicPoolsPage.vue': [
    ['Solidarité & Impact', '{{ $t(\'publicPools.badge\') }}'],
    ['Explorez les projets', '{{ $t(\'publicPools.titleStart\') }}'],
    ['qui font la différence', '{{ $t(\'publicPools.titleEnd\') }}'],
    ['Découvrez des initiatives solidaires, soutenez des causes inspirantes ou contribuez à des tontines collaboratives de confiance.', '{{ $t(\'publicPools.subtitle\') }}'],
    ['Rechercher une cause', '{{ $t(\'publicPools.searchBoxHeader\') }}'],
    ['Trouvez rapidement une cagnotte par mots-clés.', '{{ $t(\'publicPools.searchBoxDesc\') }}'],
    ['Catégories', '{{ $t(\'publicPools.categories\') }}'],
    ['Réinitialiser les filtres', '{{ $t(\'publicPools.resetFilters\') }}'],
    ['cagnottes trouvées', '{{ $t(\'publicPools.poolsFoundMultiple\') }}'],
    ['cagnotte trouvée', '{{ $t(\'publicPools.poolsFoundSingle\') }}'],
    ['Trier par :', '{{ $t(\'publicPools.sortByLabel\') }} :'],
    ['Sélectionner une catégorie', '{{ $t(\'publicPools.selectCategoryMobile\') }}'],
    ['récoltés', '{{ $t(\'publicPools.collected\') }}'],
    ['Objectif :', '{{ $t(\'publicPools.goal\') }} :'],
    ['contributeurs', '{{ $t(\'publicPools.contributorsMultiple\') }}'],
    ['contributeur', '{{ $t(\'publicPools.contributorsSingle\') }}'],
    ['financé', '{{ $t(\'publicPools.funded\') }}'],
    ['label="Détails"', ':label="$t(\'publicPools.detailsButton\')"'],
    ['label="Contribuer"', ':label="$t(\'publicPools.contributeButton\')"'],
    ['Aucune cagnotte trouvée', '{{ $t(\'publicPools.emptyTitle\') }}'],
    ['Nous n\'avons trouvé aucune cagnotte correspondant à vos critères de recherche ou de filtre.', '{{ $t(\'publicPools.emptyDesc\') }}'],
    ['Tout réinitialiser', '{{ $t(\'publicPools.emptyResetButton\') }}'],
    ['Lien de la cagnotte copié dans le presse-papiers !', '{{ $t(\'publicPools.copySuccess\') }}'],
    ['Erreur lors de la copie du lien.', '{{ $t(\'publicPools.copyError\') }}']
  ],

  // 4. Pool Detail Page
  'src/pool/pages/PoolDetailPage.vue': [
    ['Vérification de votre email...', '{{ $t(\'verifyEmail.loading\') }}'], // Fallback or matching error/loading states
    ['Retour à l\'accueil', '{{ $t(\'poolDetail.backHome\') }}'],
    ['À propos de cette cagnotte', '{{ $t(\'poolDetail.aboutPool\') }}'],
    ['Modifier', '{{ $t(\'poolDetail.edit\') }}'],
    ['Ajouter une phase', '{{ $t(\'poolDetail.addPhase\') }}'],
    ['Phases de progression', '{{ $t(\'poolDetail.phasesHeader\') }}'],
    ['Sous-cagnottes du projet', '{{ $t(\'poolDetail.subPoolsHeader\') }}'],
    ['Tableau de bord financier (Propriétaire)', '{{ $t(\'poolDetail.financialDashboard\') }}'],
    ['Solde Disponible', '{{ $t(\'poolDetail.availableBalance\') }}'],
    ['Prêt à être retiré', '{{ $t(\'poolDetail.readyToWithdraw\') }}'],
    ['En attente', '{{ $t(\'poolDetail.pending\') }}'],
    ['Contributions en cours de traitement', '{{ $t(\'poolDetail.pendingDesc\') }}'],
    ['Inviter des participants', '{{ $t(\'poolDetail.inviteParticipants\') }}'],
    ['Email de l\'invité', '{{ $t(\'poolDetail.inviteEmailPlaceholder\') }}'],
    ['Inviter', '{{ $t(\'poolDetail.inviteButton\') }}'],
    ['Membres invités', '{{ $t(\'poolDetail.invitedMembers\') }}'],
    ['Commentaires et Questions', '{{ $t(\'poolDetail.commentsHeader\') }}'],
    ['Posez une question ou laissez un message de soutien...', '{{ $t(\'poolDetail.commentPlaceholder\') }}'],
    ['Veuillez vous connecter pour laisser un commentaire.', '{{ $t(\'poolDetail.loginToComment\') }}'],
    ['Aucun commentaire pour le moment. Soyez le premier !', '{{ $t(\'poolDetail.noComments\') }}'],
    ['Comment ça marche ?', '{{ $t(\'poolDetail.howItWorksTitle\') }}'],
    ['Sécurisé', '{{ $t(\'poolDetail.howSecure\') }}'],
    ['Paiements protégés', '{{ $t(\'poolDetail.howSecureDesc\') }}'],
    ['Transparent', '{{ $t(\'poolDetail.howTransparent\') }}'],
    ['Suivi des fonds', '{{ $t(\'poolDetail.howTransparentDesc\') }}'],
    ['Collectif', '{{ $t(\'poolDetail.howCollective\') }}'],
    ['Ensemble on va plus loin', '{{ $t(\'poolDetail.howCollectiveDesc\') }}'],
    ['Informations', '{{ $t(\'poolDetail.infoTitle\') }}'],
    ['Date de creation', '{{ $t(\'poolDetail.createdAtLabel\') }}'],
    ['Visibilite', '{{ $t(\'poolDetail.visibilityLabel\') }}'],
    ['Publique', '{{ $t(\'poolDetail.visibilityPublic\') }}'],
    ['Faire un don', '{{ $t(\'poolDetail.donateDialogTitle\') }}'],
    ['Soutenez', '{{ $t(\'poolDetail.donateDialogSubtitlePrefix\') }}'],
    ['en choisissant un montant.', '{{ $t(\'poolDetail.donateDialogSubtitleSuffix\') }}'],
    ['Sélectionner la sous-cagnotte *', '{{ $t(\'poolDetail.selectSubPoolLabel\') }}'],
    ['Veuillez choisir une sous-cagnotte', '{{ $t(\'poolDetail.selectSubPoolRequired\') }}'],
    ['Montant du don (€) *', '{{ $t(\'poolDetail.donationAmountLabel\') }}'],
    ['Votre nom (facultatif)', '{{ $t(\'poolDetail.contributorNameLabel\') }}'],
    ['Votre adresse email *', '{{ $t(\'poolDetail.contributorEmailLabel\') }}'],
    ['L\'email est requis pour recevoir votre reçu de paiement', '{{ $t(\'poolDetail.emailRequiredForReceipt\') }}'],
    ['Veuillez saisir un email valide', '{{ $t(\'poolDetail.emailInvalid\') }}'],
    ['Petit message de soutien (facultatif)', '{{ $t(\'poolDetail.messageLabel\') }}'],
    ['Votre message sera affiché sur la page...', '{{ $t(\'poolDetail.messagePlaceholder\') }}'],
    ['Faire ce don de manière anonyme', '{{ $t(\'poolDetail.anonymousToggle\') }}'],
    ['Sélectionnez votre moyen de paiement :', '{{ $t(\'poolDetail.selectPaymentMethod\') }} :'],
    ['Date limite :', '{{ $t(\'poolDetail.deadlineLabel\') }} :'],
    ['sur {{ pool.goalAmount }} €', '{{ $t(\'poolDetail.ofGoal\') }} {{ pool.goalAmount }} €'],
    ['complété', '{{ $t(\'poolDetail.completedPercentSuffix\') }}'],
    ['participants', '{{ $t(\'poolDetail.participants\') }}'],
    ['Contribuer maintenant', '{{ $t(\'poolDetail.contributeNow\') }}'],
    ['Partager', '{{ $t(\'poolDetail.share\') }}'],
    ['Derniers donateurs', '{{ $t(\'poolDetail.lastDonors\') }}'],
    ['Donateur anonyme', '{{ $t(\'poolDetail.anonymousDonor\') }}'],
    ['Soyez le premier à contribuer !', '{{ $t(\'poolDetail.firstContributorPrompt\') }}']
  ],

  // 5. Edit Pool Page
  'src/pool/pages/EditPoolPage.vue': [
    ['Modifier ma cagnotte', '{{ $t(\'editPool.title\') }}'],
    ['Mettez à jour les informations et le statut de votre projet.', '{{ $t(\'editPool.subtitle\') }}'],
    ['Chargement des détails de la cagnotte...', '{{ $t(\'editPool.loading\') }}'],
    ['Détails généraux de la cagnotte', '{{ $t(\'editPool.generalDetails\') }}'],
    ['Titre de la cagnotte *', '{{ $t(\'editPool.titleLabel\') }}'],
    ['Objectif financier (€) *', '{{ $t(\'editPool.goalLabel\') }}'],
    ['Statut *', '{{ $t(\'editPool.statusLabel\') }}'],
    ['Image & Média', '{{ $t(\'editPool.mediaHeader\') }}'],
    ['Changer l\'image illustrative', '{{ $t(\'editPool.changeImageLabel\') }}'],
    ['Lien Vidéo YouTube ou Vimeo (Optionnel)', '{{ $t(\'editPool.videoUrlLabel\') }}'],
    ['Annuler', '{{ $t(\'editPool.cancel\') }}'],
    ['Enregistrer les modifications', '{{ $t(\'editPool.saveChanges\') }}'],
    ['Cagnotte mise à jour avec succès !', '{{ $t(\'editPool.success\') }}'],
    ['Erreur lors de la récupération des détails de la cagnotte', '{{ $t(\'editPool.errorFetch\') }}'],
    ['Vous n\'êtes pas autorisé à modifier cette cagnotte', '{{ $t(\'editPool.errorUnauthorized\') }}']
  ],

  // 6. Profile Page
  'src/user/pages/ProfilePage.vue': [
    ['Membre depuis', '{{ $t(\'profile.memberSince\') }}'],
    ['Paramètres du compte', '{{ $t(\'profile.accountSettings\') }}'],
    ['Nom complet', '{{ $t(\'profile.fullName\') }}'],
    ['Le nom est obligatoire', '{{ $t(\'profile.nameRequired\') }}'],
    ['Adresse Email', '{{ $t(\'profile.emailAddress\') }}'],
    ['Enregistrer les modifications', '{{ $t(\'profile.saveChanges\') }}'],
    ['Contributions totales', '{{ $t(\'profile.totalContributions\') }}'],
    ['Sécurité', '{{ $t(\'profile.securityHeader\') }}'],
    ['Changer le mot de passe', '{{ $t(\'profile.changePassword\') }}'],
    ['Se déconnecter', '{{ $t(\'profile.logout\') }}'],
    ['Ancien mot de passe', '{{ $t(\'profile.oldPassword\') }}'],
    ['Requis', '{{ $t(\'profile.required\') }}'],
    ['Nouveau mot de passe', '{{ $t(\'profile.newPassword\') }}'],
    ['Minimum 6 caractères', '{{ $t(\'profile.minPasswordLength\') }}'],
    ['Confirmer le nouveau mot de passe', '{{ $t(\'profile.confirmNewPassword\') }}'],
    ['Les mots de passe ne correspondent pas', '{{ $t(\'profile.passwordsMismatch\') }}'],
    ['Annuler', '{{ $t(\'profile.cancel\') }}'],
    ['Mettre à jour', '{{ $t(\'profile.update\') }}'],
    ['Profil mis à jour avec succès !', '{{ $t(\'profile.successUpdate\') }}'],
    ['Mot de passe modifié avec succès !', '{{ $t(\'profile.successPassword\') }}']
  ],

  // 7. Verify Email Page
  'src/user/pages/auth/VerifyEmailPage.vue': [
    ['Vérification de votre email...', '{{ $t(\'verifyEmail.loading\') }}'],
    ['Félicitations !', '{{ $t(\'verifyEmail.congrats\') }}'],
    ['Votre email a été vérifié avec succès.', '{{ $t(\'verifyEmail.successDesc\') }}'],
    ['Se connecter', '{{ $t(\'verifyEmail.loginButton\') }}'],
    ['Oups !', '{{ $t(\'verifyEmail.errorTitle\') }}'],
    ['Le lien de vérification est invalide ou a expiré.', '{{ $t(\'verifyEmail.errorDesc\') }}'],
    ['Retour à l\'accueil', '{{ $t(\'verifyEmail.backHome\') }}']
  ],

  // 8. Accept Invitation Page
  'src/pool/pages/AcceptInvitationPage.vue': [
    ['Traitement de votre invitation...', '{{ $t(\'acceptInvitation.loading\') }}'],
    ['Invitation Acceptée !', '{{ $t(\'acceptInvitation.successTitle\') }}'],
    ['Vous avez rejoint la cagnotte avec succès.', '{{ $t(\'acceptInvitation.successDesc\') }}'],
    ['Aller à la cagnotte', '{{ $t(\'acceptInvitation.goToPoolButton\') }}'],
    ['Erreur', '{{ $t(\'acceptInvitation.errorTitle\') }}'],
    ['Retour à l\'accueil', '{{ $t(\'acceptInvitation.backHome\') }}'],
    ['Invitation acceptée avec succès !', '{{ $t(\'acceptInvitation.successToast\') }}']
  ],

  // 9. Payment Success Page
  'src/pool/pages/PaymentSuccessPage.vue': [
    ['Validation en cours...', '{{ $t(\'paymentSuccess.loadingTitle\') }}'],
    ['Nous sécurisons et enregistrons votre contribution.', '{{ $t(\'paymentSuccess.loadingDesc\') }}'],
    ['Paiement Réussi !', '{{ $t(\'paymentSuccess.successTitle\') }}'],
    ['Votre générosité fait la différence. Merci infiniment pour votre don !', '{{ $t(\'paymentSuccess.successDesc\') }}'],
    ['Méthode de paiement :', '{{ $t(\'paymentSuccess.paymentMethod\') }} :'],
    ['Identifiant de transaction :', '{{ $t(\'paymentSuccess.transactionId\') }} :'],
    ['Retourner à la cagnotte', '{{ $t(\'paymentSuccess.backToPoolButton\') }}'],
    ['Redirection automatique dans', '{{ $t(\'paymentSuccess.redirectTimer\') }}'],
    ['secondes...', '{{ $t(\'paymentSuccess.seconds\') }}...'],
    ['Erreur de Paiement', '{{ $t(\'paymentSuccess.errorTitle\') }}'],
    ['Nous n\'avons pu valider votre paiement. Veuillez vérifier vos informations ou réessayer ultérieurement.', '{{ $t(\'paymentSuccess.errorDesc\') }}'],
    ['Réessayer la contribution', '{{ $t(\'paymentSuccess.retryButton\') }}']
  ],

  // 10. Admin Dashboard Page
  'src/user/pages/AdminDashboardPage.vue': [
    ['Tableau de Bord Admin', '{{ $t(\'adminDashboard.title\') }}'],
    ['Gérez vos utilisateurs, cagnottes, rôles et suivez les statistiques de la plateforme en temps réel.', '{{ $t(\'adminDashboard.subtitle\') }}'],
    ['Utilisateurs inscrits', '{{ $t(\'adminDashboard.registeredUsers\') }}'],
    ['cette semaine', '{{ $t(\'adminDashboard.thisWeek\') }}'],
    ['Cagnottes Créées', '{{ $t(\'adminDashboard.poolsCreated\') }}'],
    ['actives', '{{ $t(\'adminDashboard.active\') }}'],
    ['terminées', '{{ $t(\'adminDashboard.completed\') }}'],
    ['Fonds Collectés', '{{ $t(\'adminDashboard.fundsCollected\') }}'],
    ['Moyenne de', '{{ $t(\'adminDashboard.averageOf\') }}'],
    ['par projet', '{{ $t(\'adminDashboard.perProject\') }}'],
    ['Taux de Réussite', '{{ $t(\'adminDashboard.successRate\') }}'],
    ['Objectifs cibles atteints', '{{ $t(\'adminDashboard.goalsReached\') }}'],
    ['Raccourcis de Gestion', '{{ $t(\'adminDashboard.shortcutsHeader\') }}'],
    ['Gérer les comptes', '{{ $t(\'adminDashboard.manageAccounts\') }}'],
    ['Ajouter, éditer, désactiver les utilisateurs', '{{ $t(\'adminDashboard.manageAccountsDesc\') }}'],
    ['Gérer les cagnottes', '{{ $t(\'adminDashboard.managePools\') }}'],
    ['Modérer les collectes de la plateforme', '{{ $t(\'adminDashboard.managePoolsDesc\') }}'],
    ['Rôles & Permissions', '{{ $t(\'adminDashboard.manageRoles\') }}'],
    ['Gérer les niveaux d\'accès de sécurité', '{{ $t(\'adminDashboard.manageRolesDesc\') }}'],
    ['Derniers Projets Créés', '{{ $t(\'adminDashboard.lastProjectsHeader\') }}'],
    ['Voir tout', '{{ $t(\'adminDashboard.viewAll\') }}'],
    ['Créateur:', '{{ $t(\'adminDashboard.creatorLabel\') }} :'],
    ['sur', '{{ $t(\'adminDashboard.ofGoal\') }}'],
    ['Aucune cagnotte créée.', '{{ $t(\'adminDashboard.noPools\') }}']
  ],

  // 11. User List Page
  'src/user/pages/UserListPage.vue': [
    ['Gestion des Utilisateurs', '{{ $t(\'userList.title\') }}'],
    ['label="Ajouter"', ':label="$t(\'userList.addButton\')"'],
    ['Nom Complet', 'fullName'], // mapped via object/columns in data, we can define direct column header translating in computed if we want
    ['Statut', 'status']
  ],

  // 12. User Form Dialog
  'src/user/components/UserFormDialog.vue': [
    ['isEdit ? \'Modifier l\'\'Utilisateur\' : \'Ajouter un Utilisateur\'', 'isEdit ? $t(\'userList.editUserTitle\') : $t(\'userList.addUserTitle\')'],
    ['label="Nom complet *"', ':label="$t(\'auth.fullName\') + \' *\'"'],
    ['Veuillez saisir le nom complet', '{{ $t(\'auth.fullNameRequired\') }}'],
    ['label="Email *"', ':label="$t(\'auth.email\') + \' *\'"'],
    ['Veuillez saisir un email', '{{ $t(\'auth.emailRequired\') }}'],
    ['isEdit ? \'Nouveau mot de passe (laisser vide pour ne pas changer)\' : \'Mot de passe *\'', 'isEdit ? $t(\'userList.newPasswordLabel\') : $t(\'auth.password\') + \' *\''],
    ['Veuillez saisir un mot de passe', '{{ $t(\'auth.passwordRequired\') }}'],
    ['label="Statut *"', ':label="$t(\'dashboard.status\') + \' *\'"'],
    ['Veuillez sélectionner un statut', '{{ $t(\'userList.selectStatusRequired\') }}'],
    ['Rôles assignés', '{{ $t(\'userList.assignedRoles\') }}'],
    ['label="Ajouter un rôle"', ':label="$t(\'userList.addRoleLabel\')"'],
    ['Aucun rôle assigné', '{{ $t(\'userList.noRolesAssigned\') }}'],
    ['label="Annuler"', ':label="$t(\'profile.cancel\')"'],
    ['label="Enregistrer"', ':label="$t(\'profile.update\')"']
  ],

  // 13. Role List Page
  'src/user/pages/RoleListPage.vue': [
    ['Rôles & Permissions', '{{ $t(\'roleList.title\') }}'],
    ['Gérerez la sécurité et les accès en glissant-déposant les permissions disponibles sur les rôles.', '{{ $t(\'roleList.subtitle\') }}'],
    ['label="Nouveau Rôle"', ':label="$t(\'roleList.newRoleButton\')"'],
    ['label="Nouvelle Permission"', ':label="$t(\'roleList.newPermissionButton\')"'],
    ['Permissions Disponibles', '{{ $t(\'roleList.availablePermissions\') }}'],
    ['placeholder="Rechercher une permission..."', ':placeholder="$t(\'roleList.searchPlaceholder\')"'],
    ['Aucune description', '{{ $t(\'roleList.noDescription\') }}'],
    ['Supprimer la permission', '{{ $t(\'roleList.deletePermissionTooltip\') }}'],
    ['Aucune permission trouvée.', '{{ $t(\'roleList.noPermissionsFound\') }}'],
    ['Rôles Actifs', '{{ $t(\'roleList.activeRoles\') }}'],
    ['Modifier le rôle', '{{ $t(\'roleList.editRoleTooltip\') }}'],
    ['Supprimer le rôle', '{{ $t(\'roleList.deleteRoleTooltip\') }}'],
    ['Permissions associées :', '{{ $t(\'roleList.associatedPermissions\') }} :'],
    ['Glissez des permissions ici', '{{ $t(\'roleList.dragPermissionsHint\') }}'],
    ['Déposer pour ajouter', '{{ $t(\'roleList.dropToAdd\') }}'],
    ['Supprimer le rôle', '{{ $t(\'roleList.deleteRoleTitle\') }}'],
    ['Voulez-vous vraiment supprimer définitivement le rôle', '{{ $t(\'roleList.deleteRoleConfirm\') }}'],
    ['Supprimer la permission', '{{ $t(\'roleList.deletePermissionTitle\') }}'],
    ['Voulez-vous vraiment supprimer définitivement la permission', '{{ $t(\'roleList.deletePermissionConfirm\') }}']
  ],

  // 14. Admin Pools Page
  'src/pool/pages/AdminPoolsPage.vue': [
    ['Gestion des Cagnottes', '{{ $t(\'adminPools.title\') }}'],
    ['Visualisez, modérez et modifiez toutes les cagnottes créées sur la plateforme.', '{{ $t(\'adminPools.subtitle\') }}'],
    ['label="Créer une cagnotte"', ':label="$t(\'home.createButton\')"'],
    ['label="Rechercher par titre ou créateur..."', ':label="$t(\'adminPools.searchPlaceholder\')"'],
    ['label="Filtrer par statut"', ':label="$t(\'adminPools.statusFilterPlaceholder\')"'],
    ['label="Réinitialiser"', ':label="$t(\'adminPools.resetButton\')"'],
    ['Aperçu', 'image'], // table headers
    ['Titre de la cagnotte', 'title'],
    ['Créateur', 'owner'],
    ['Catégorie', 'category'],
    ['Collecte', 'progress'],
    ['Statut', 'status'],
    ['Actions', 'actions'],
    ['Voir la page', '{{ $t(\'adminPools.viewPageTooltip\') }}'],
    ['Modifier', '{{ $t(\'adminPools.editTooltip\') }}'],
    ['Marquer terminée', '{{ $t(\'adminPools.markCompletedTooltip\') }}'],
    ['Suspendre', '{{ $t(\'adminPools.suspendTooltip\') }}'],
    ['Réactiver', '{{ $t(\'adminPools.reactivateTooltip\') }}'],
    ['Confirmer la modification', '{{ $t(\'adminPools.confirmDialogTitle\') }}'],
    ['Voulez-vous modifier le statut de la cagnotte', '{{ $t(\'adminPools.confirmDialogMessage\') }}']
  ]
};

// 2. Execute replacements on Vue files
for (const [relativePath, pairs] of Object.entries(replacements)) {
  const fullPath = path.join(__dirname, relativePath);
  if (!fs.existsSync(fullPath)) {
    console.warn(`File not found: ${fullPath}`);
    continue;
  }

  let content = fs.readFileSync(fullPath, 'utf8');
  let original = content;

  for (const [target, replacement] of pairs) {
    if (content.includes(target)) {
      // Clean target replace (using split/join to replace all occurrences if any)
      content = content.split(target).join(replacement);
    }
  }

  if (content !== original) {
    fs.writeFileSync(fullPath, content, 'utf8');
    console.log(`Successfully localized file: ${relativePath}`);
  } else {
    console.log(`No changes made to: ${relativePath} (targets might already be replaced or missing)`);
  }
}

console.log("Translation replacements complete!");
