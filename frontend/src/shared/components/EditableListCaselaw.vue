<script lang="ts" setup generic="T extends ListItem">
import type { Component, Ref } from "vue"
import { ref, watch, onMounted, nextTick } from "vue"
import ListItem from "@/domain/editableListItem"
import DataSetSummary from "@/shared/components/DataSetSummary.vue"

interface Props {
  editComponent: Component
  summaryComponent?: Component
  modelValue?: T[]
  defaultValue: T
  disableMultiEntry?: boolean
  addEntryLabel?: string
  noHorizontalSeparators?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  summaryComponent: DataSetSummary,
  modelValue: () => [],
  disableMultiEntry: false,
  addEntryLabel: "Weitere Angabe",
  noHorizontalSeparators: false,
})

const emit = defineEmits<{
  "update:modelValue": [value: T[]]
  deleteLastEntry: [void]
}>()

const modelValueList = ref<T[]>([]) as Ref<T[]>
const localList = ref([...props.modelValue]) as Ref<T[]>
const elementList = ref<HTMLElement[]>([])
const editIndex = ref<number | undefined>(undefined)

const focusFirstFocusableElementOfCurrentEditElement = async () => {
  await nextTick()

  if (!editIndex.value) {
    return
  }

  const editElement = elementList.value[editIndex.value]
  editElement
    ?.querySelector<HTMLElement>(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])',
    )
    ?.focus()
}
watch(editIndex, focusFirstFocusableElementOfCurrentEditElement)

function setEditIndex(index?: number) {
  editIndex.value = index
}

function addNewListEntry() {
  const { defaultValue } = props
  const newEntry =
    typeof defaultValue === "object" ? { ...defaultValue } : defaultValue
  localList.value.push(newEntry)
  editIndex.value = localList.value.length - 1
}

function removeListEntry(index: number) {
  localList.value.splice(index, 1)
  modelValueList.value = localList.value

  if (editIndex.value !== undefined && index < editIndex.value) {
    editIndex.value -= 1
  }
}

function updateModel() {
  setEditIndex()
  modelValueList.value = localList.value
}

watch(
  () => props.modelValue,
  () => {
    modelValueList.value = props.modelValue
  },
  { immediate: true, deep: true },
)

watch(
  modelValueList,
  () => {
    emit("update:modelValue", modelValueList.value)
  },
  { deep: true, immediate: true },
)

watch(
  localList,
  () => {
    setEmptyEntryInEditMode()
    if (localList.value.length == 0) {
      addNewListEntry()
    }
  },
  { deep: true, immediate: true },
)

function setEmptyEntryInEditMode(): void {
  const emptyItemIndex = localList.value.findIndex((element) => element.isEmpty)
  if (emptyItemIndex !== -1) setEditIndex(emptyItemIndex)
}

onMounted(() => {
  localList.value = [...props.modelValue]
  if (localList.value.length == 0) {
    addNewListEntry()
  }
})
</script>

<template>
  <div class="w-full">
    <div
      v-for="(entry, index) in localList"
      :key="index"
      ref="elementList"
      aria-label="Listen Eintrag"
    >
      <div
        v-if="index !== editIndex"
        :key="index"
        class="group flex cursor-pointer items-center justify-between gap-8 border-b-1 border-gray-400 py-8 group-first:pt-0"
        :class="{ '!border-none': noHorizontalSeparators }"
      >
        <component
          :is="summaryComponent"
          class="focus:outline-none focus-visible:outline-blue-800"
          :data="entry"
          tabindex="0"
          @click="
            entry.hasForeignSource
              ? (e: Event) => e.preventDefault()
              : setEditIndex(index)
          "
          @keypress.enter="setEditIndex(index)"
        />

        <div class="flex gap-8">
          <button
            v-if="!entry.isReadOnly"
            aria-label="Eintrag bearbeiten"
            class="material-icons p-2 text-blue-800 outline-none outline-offset-2 hover:bg-blue-200 focus:outline-2 focus:outline-blue-800 active:bg-blue-500 active:outline-none"
            @click="setEditIndex(index)"
          >
            edit_note
          </button>

          <button
            aria-label="Eintrag löschen"
            class="material-icons p-2 text-blue-800 outline-none outline-offset-2 hover:bg-blue-200 focus:outline-2 focus:outline-blue-800 active:bg-blue-500 active:outline-none"
            @click="removeListEntry(index)"
          >
            delete_outline
          </button>
        </div>
      </div>

      <component
        :is="editComponent"
        v-else
        v-model="localList[index]"
        class="py-16 group-first:pt-0"
        :model-value-list="localList"
        @add-entry="updateModel"
      />
    </div>

    <button
      v-if="!disableMultiEntry && editIndex === undefined"
      aria-label="Weitere Angabe"
      class="add-button gap-0.5 mt-16 flex items-center whitespace-nowrap bg-blue-300 px-8 py-2 text-14 font-bold leading-18 text-blue-800 outline-none outline-0 outline-offset-4 outline-blue-800 hover:bg-blue-800 hover:text-white focus:outline-4"
      @click="addNewListEntry"
    >
      <span class="material-icons text-14">add</span>
      Weitere Angabe
    </button>
  </div>
</template>

<style lang="scss" scoped>
.add-button {
  &:focus:not(:focus-visible) {
    @apply outline-transparent;
  }
}
</style>
