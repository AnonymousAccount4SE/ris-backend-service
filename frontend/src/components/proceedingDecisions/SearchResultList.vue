<script setup lang="ts">
import InlineDecision from "./InlineDecision.vue"
import LinkedDocumentUnit from "@/domain/linkedDocumentUnit"
import TextButton from "@/shared/components/input/TextButton.vue"

defineProps<{
  searchResults?: SearchResults<LinkedDocumentUnit>
}>()

const emits =
  defineEmits<(event: "linkDecision", decision: LinkedDocumentUnit) => void>()
</script>

<script lang="ts">
export type SearchResults<Type extends LinkedDocumentUnit> = {
  decision: Type
  isLinked: boolean
}[]
</script>

<template>
  <div>
    <span v-if="!searchResults?.length" class="ds-label-01-bold"
      >Suche hat keine Treffer ergeben</span
    >
    <div class="mt-16 table">
      <div
        v-for="searchResult in searchResults"
        :key="searchResult.decision.uuid"
        class="mb-24 mt-12 table-row"
      >
        <div class="table-cell">
          <InlineDecision :decision="searchResult.decision" />
        </div>
        <div class="table-cell p-8">
          <TextButton
            aria-label="Treffer übernehmen"
            class="ml-24"
            :disabled="searchResult.isLinked"
            label="Übernehmen"
            @click="emits('linkDecision', searchResult.decision)"
          />
        </div>
        <span
          v-if="searchResult.isLinked"
          class="ds-label-03-reg ml-24 rounded-full bg-green-700 px-24 py-4 text-white"
          >Bereits hinzugefügt</span
        >
      </div>
    </div>
  </div>
</template>
