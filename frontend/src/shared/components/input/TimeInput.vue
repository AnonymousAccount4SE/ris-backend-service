<script lang="ts" setup>
import TextInput from "@/shared/components/input/TextInput.vue"

defineProps<{
  id: string
  modelValue: string
}>()

const emit = defineEmits<{
  "update:modelValue": [value?: string]
}>()
</script>

<template>
  <TextInput
    :id="id"
    :aria-label="($attrs.ariaLabel as string) ?? ''"
    class="ds-input"
    :model-value="modelValue"
    placeholder="HH:MM"
    role="textbox"
    type="time"
    @update:model-value="emit('update:modelValue', $event)"
  />
</template>
