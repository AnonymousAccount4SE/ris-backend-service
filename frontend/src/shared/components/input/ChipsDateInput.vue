<script lang="ts" setup>
import dayjs from "dayjs"
import customParseFormat from "dayjs/plugin/customParseFormat"
import { vMaska, MaskaDetail } from "maska"
import { ref, watch, computed } from "vue"
import { ValidationError } from "@/shared/components/input/types"

const props = defineProps<Props>()
const emit = defineEmits<{
  "update:modelValue": [value?: string[]]
  "update:validationError": [value?: ValidationError]
  input: [value: Event]
}>()

interface Props {
  id: string
  value?: string[]
  modelValue?: string[]
  ariaLabel: string
  placeholder?: string
  isFutureDate?: boolean
  validationError?: ValidationError
}

const chips = ref<string[]>(props.modelValue ?? [])
const currentInput = ref<string | undefined>()
const currentInputField = ref<HTMLInputElement>()
const focusedItemIndex = ref<number>()
const containerRef = ref<HTMLElement>()

watch(
  props,
  () =>
    (chips.value = props.modelValue
      ? props.modelValue.map((value) => dayjs(value).format("DD.MM.YYYY"))
      : []),
  {
    immediate: true,
  },
)

function updateModelValue() {
  emit(
    "update:modelValue",
    chips.value.length === 0
      ? undefined
      : chips.value.map((value) =>
          dayjs(value, "DD.MM.YYYY", true).toISOString(),
        ),
  )
}

function saveChip() {
  if (!hasError.value && currentInput.value && currentInput.value.length > 0) {
    chips.value.push(currentInput.value)
    updateModelValue()
    currentInput.value = undefined
    resetFocus()
  }
}

function deleteChip(index: number) {
  chips.value.splice(index, 1)
  updateModelValue()
  resetFocus()
}

function resetFocus() {
  currentInputField.value?.blur()
  focusedItemIndex.value = undefined
  currentInputField.value?.focus()
}

function backspaceDelete() {
  if (currentInput.value === undefined) {
    chips.value.splice(chips.value.length - 1)
    updateModelValue()
    resetFocus()
  } else currentInput.value = undefined
}

function enterDelete() {
  if (focusedItemIndex.value !== undefined) {
    currentInput.value = undefined
    chips.value.splice(focusedItemIndex.value, 1)
    // bring focus on second last item if last item was deleted
    if (focusedItemIndex.value === chips.value.length) {
      focusPrevious()
    }
    if (focusedItemIndex.value === 0) {
      resetFocus()
    }
  }

  updateModelValue()
}

const focusPrevious = () => {
  if (
    (currentInput.value && currentInput.value.length > 0) ||
    focusedItemIndex.value === 0
  ) {
    return
  }
  focusedItemIndex.value =
    focusedItemIndex.value === undefined
      ? chips.value.length - 1
      : focusedItemIndex.value - 1
  const prev = containerRef.value?.children[
    focusedItemIndex.value
  ] as HTMLElement
  if (prev) prev.focus()
}

const focusNext = () => {
  if (
    (currentInput.value && currentInput.value.length > 0) ||
    focusedItemIndex.value === undefined
  ) {
    return
  }
  if (focusedItemIndex.value == chips.value.length - 1) {
    resetFocus()
    return
  }
  focusedItemIndex.value =
    focusedItemIndex.value === undefined ? 0 : focusedItemIndex.value + 1
  const next = containerRef.value?.children[
    focusedItemIndex.value
  ] as HTMLElement
  if (next) next.focus()
}

const setFocusedItemIndex = (index: number) => {
  focusedItemIndex.value = index
}

const inputCompleted = ref<boolean>(false)

dayjs.extend(customParseFormat)

const isValidDate = computed(() => {
  return dayjs(currentInput.value, "DD.MM.YYYY", true).isValid()
})

const isInPast = computed(() => {
  if (props.isFutureDate) return true
  return dayjs(currentInput.value, "DD.MM.YYYY", true).isBefore(dayjs())
})

const onMaska = (event: CustomEvent<MaskaDetail>) => {
  inputCompleted.value = event.detail.completed
}

const hasError = computed(
  () =>
    props.validationError ||
    (inputCompleted.value && !isInPast.value && !props.isFutureDate) ||
    (inputCompleted.value && !isValidDate.value),
)

const conditionalClasses = computed(() => ({
  input__error: props.validationError ?? hasError.value,
}))

function validateInput() {
  if (inputCompleted.value) {
    if (isValidDate.value) {
      // if valid date, check for future dates
      if (!isInPast.value && !props.isFutureDate && isValidDate.value)
        emit("update:validationError", {
          defaultMessage:
            "Das " + props.ariaLabel + " darf nicht in der Zukunft liegen",
          field: props.id,
        })
      else emit("update:validationError", undefined)
    } else {
      emit("update:validationError", {
        defaultMessage: "Kein valides Datum",
        field: props.id,
      })
    }
  } else {
    if (currentInput.value) {
      emit("update:validationError", {
        defaultMessage: "Unvollständiges Datum",
        field: props.id,
      })
    } else {
      emit("update:validationError", undefined)
    }
  }
}

function inputDelete() {
  emit("update:validationError", undefined)
}

function onBlur() {
  validateInput()
}

watch(inputCompleted, () => {
  validateInput()
})
</script>

<template>
  <div class="input bg-white" :class="conditionalClasses">
    <div ref="containerRef" class="flex flex-row flex-wrap" tabindex="-1">
      <div
        v-for="(chip, i) in chips"
        :key="i"
        aria-label="chip"
        class="chip ds-body-01-reg bg-blue-500"
        tabindex="0"
        @click="setFocusedItemIndex(i)"
        @keydown.delete="backspaceDelete"
        @keypress.enter="enterDelete"
        @keyup.left="focusPrevious"
        @keyup.right="focusNext"
      >
        <div class="label-wrapper">{{ chip }}</div>

        <div class="icon-wrapper">
          <em
            aria-Label="Löschen"
            class="material-icons"
            @click="deleteChip(i)"
            @keydown.enter="deleteChip(i)"
            >clear</em
          >
        </div>
      </div>
    </div>

    <input
      :id="id"
      ref="currentInputField"
      v-model="currentInput"
      v-maska
      :aria-label="ariaLabel"
      :class="conditionalClasses"
      data-maska="##.##.####"
      placeholder="DD.MM.YYYY"
      @blur="onBlur"
      @keydown.delete="inputDelete"
      @keypress.enter="saveChip"
      @keyup.left="focusPrevious"
      @keyup.right="focusNext"
      @maska="onMaska"
    />
  </div>
</template>

<style lang="scss" scoped>
.input {
  display: flex;
  width: 100%;
  min-height: 3.75rem;
  flex-wrap: wrap;
  align-content: space-between;
  padding: 12px 16px 4px;
  @apply border-2 border-solid border-blue-800 uppercase;

  &:focus {
    outline: none;
  }

  &:autofill {
    @apply text-inherit shadow-white;
  }

  &:autofill:focus {
    @apply text-inherit shadow-white;
  }

  &__error {
    width: 100%;
    @apply border-red-800 bg-red-200;

    &:autofill {
      @apply text-inherit shadow-error;
    }

    &:autofill:focus {
      @apply text-inherit shadow-error;
    }
  }

  .chip {
    display: flex;
    align-items: center;
    border-radius: 10px;
    margin: 0 8px 8px 0;

    .icon-wrapper {
      display: flex;
      padding: 4px 3px;
      border-radius: 0 10px 10px 0;

      em {
        cursor: pointer;
      }
    }

    .label-wrapper {
      display: flex;
      padding: 3px 0 3px 8px;
      margin-right: 8px;
    }

    &:focus {
      outline: none;

      .icon-wrapper {
        @apply bg-blue-900;

        em {
          color: white;
        }
      }
    }
  }

  input {
    min-width: 8.74rem;
    flex: 1 1 auto;
    border: none;
    margin-bottom: 8px;
    outline: none;
    text-transform: uppercase;
  }
}
</style>
