<script lang="ts" setup>
import { computed } from "vue"
import { Texts } from "../domain/documentUnit"
import TextEditor from "../shared/components/input/TextEditor.vue"
import { texts as textsFields } from "@/fields/caselaw"

const props = defineProps<{ texts: Texts }>()

const emit = defineEmits<{
  updateValue: [updatedValue: [keyof Texts, string]]
}>()

const data = computed(() =>
  textsFields.map((item) => {
    return {
      id: item.name as keyof Texts,
      name: item.name,
      label: item.label,
      aria: item.label,
      fieldSize: item.fieldSize,
      value: props.texts[item.name as keyof Texts],
    }
  }),
)
</script>

<template>
  <div class="mb-[4rem]">
    <h1 class="ds-heading-02-reg mb-[1rem]">Kurz- & Langtexte</h1>

    <div class="flex flex-col gap-36">
      <div v-for="item in data" :key="item.id" class="">
        <label class="ds-label-02-reg mb-2" :for="item.id">{{
          item.label
        }}</label>

        <TextEditor
          :id="item.id"
          :aria-label="item.aria"
          class="outline outline-2 outline-blue-900"
          editable
          :field-size="item.fieldSize"
          :value="item.value"
          @update-value="emit('updateValue', [item.id, $event])"
        />
      </div>
    </div>
  </div>
</template>
