<script lang="ts" setup>
import FieldOfLawListEntry from "./FieldOfLawListEntry.vue"
import { FieldOfLawNode } from "@/domain/fieldOfLaw"

const props = defineProps<{
  selectedFieldsOfLaw: FieldOfLawNode[]
}>()

const emit = defineEmits<{
  "remove-from-list": [identifier: string]
  "node-clicked": [identifier: string]
  "linkedField:clicked": [identifier: string]
}>()
</script>

<template>
  <div class="pt-20">
    <div v-if="!props.selectedFieldsOfLaw.length">
      Die Liste ist aktuell leer
    </div>
    <div v-else>
      <hr class="mt-20 w-full border-blue-500" />
      <FieldOfLawListEntry
        v-for="fieldOfLaw in props.selectedFieldsOfLaw"
        :key="fieldOfLaw.identifier"
        :field-of-law="fieldOfLaw"
        show-bin
        @linked-field:clicked="(identifier) => emit('node-clicked', identifier)"
        @node-clicked="emit('node-clicked', fieldOfLaw.identifier)"
        @remove-from-list="emit('remove-from-list', fieldOfLaw.identifier)"
      />
    </div>
  </div>
</template>
