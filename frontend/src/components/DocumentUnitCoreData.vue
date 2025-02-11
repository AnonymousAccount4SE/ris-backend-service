<script lang="ts" setup>
import { computed, onMounted, ref, toRefs } from "vue"
import { CoreData } from "../domain/documentUnit"
import InputGroup from "../shared/components/input/InputGroup.vue"
import { courtFields, coreDataFields } from "@/fields/caselaw"
import { ValidationError } from "@/shared/components/input/types"
import { useTransformNestedData } from "@/shared/composables/useTransformNestedData"

interface Props {
  modelValue: CoreData
  validationErrors?: ValidationError[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  updateDocumentUnit: [void]
  "update:modelValue": [value: CoreData]
}>()

const { modelValue } = toRefs(props)

const values = useTransformNestedData(modelValue, coreDataFields, emit)
const courtValues = useTransformNestedData(modelValue, courtFields, emit)

const containerWidth = ref()
const columnCount = computed(() => (containerWidth.value < 600 ? 1 : 2))

onMounted(() => {
  const editorContainer = document.querySelector(".core-data")
  if (editorContainer != null) resizeObserver.observe(editorContainer)
})

const resizeObserver = new ResizeObserver((entries) => {
  for (const entry of entries) {
    containerWidth.value = entry.contentRect.width
  }
})
</script>

<template>
  <div v-if="!modelValue">Loading...</div>

  <div v-else class="mb-[4rem]">
    <h1 class="core-data ds-heading-02-reg mb-[1rem]">Stammdaten</h1>

    <InputGroup
      v-model="courtValues"
      :column-count="1"
      :fields="courtFields"
      :validation-errors="props.validationErrors"
    />
    <InputGroup
      v-model="values"
      :column-count="columnCount"
      :fields="coreDataFields"
      :validation-errors="props.validationErrors"
    />
    <div class="mt-4">* Pflichtfelder zum Veröffentlichen</div>
  </div>
</template>
