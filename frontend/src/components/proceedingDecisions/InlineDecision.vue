<script setup lang="ts">
import LinkedDocumentUnit from "@/domain/linkedDocumentUnit"

defineProps<{
  decision: LinkedDocumentUnit
}>()
</script>

<template>
  <div>
    <router-link
      v-if="decision.hasForeignSource"
      class="ds-link-01-bold underline"
      tabindex="-1"
      target="_blank"
      :to="{
        name: 'caselaw-documentUnit-documentNumber-categories',
        params: { documentNumber: decision.documentNumber },
      }"
    >
      <button class="underline">
        {{ decision.renderDecision }}
      </button>
    </router-link>

    <span v-else class="ds-label-02-reg">{{ decision.renderDecision }}</span>
  </div>
</template>
