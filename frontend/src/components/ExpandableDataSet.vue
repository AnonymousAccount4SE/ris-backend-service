<script lang="ts" setup>
import type { Component } from "vue"
import { ref } from "vue"
import ExpandableContent from "@/components/ExpandableContent.vue"
import DataSetSummary from "@/shared/components/DataSetSummary.vue"
import TextButton from "@/shared/components/input/TextButton.vue"

interface Props {
  title: string
  dataSet: any // eslint-disable-line @typescript-eslint/no-explicit-any
  summaryComponent?: Component
  asColumn?: boolean
  fallbackText?: string
  borderBottom?: boolean
}

withDefaults(defineProps<Props>(), {
  summaryComponent: DataSetSummary,
  asColumn: false,
  fallbackText: undefined,
  borderBottom: false,
})

const isExpanded = ref(false)

function collapse(): void {
  isExpanded.value = false
}
</script>

<template>
  <ExpandableContent
    v-model:is-expanded="isExpanded"
    class="border-gray-400 bg-white p-16 pt-20"
    :class="{
      'hover:border-blue-500 hover:bg-blue-200': !isExpanded,
      'border-b': borderBottom,
    }"
    close-icon-name="expand_less"
    open-icon-name="expand_more"
  >
    <template #header>
      <div class="flex w-full gap-8" :class="{ 'flex-col': asColumn }">
        <h2
          class="ds-label-02-bold w-[15rem] flex-none text-left"
          :class="{ 'mb-24': asColumn && !isExpanded }"
        >
          {{ title }}
        </h2>
        <span
          v-if="dataSet?.length === 0 && fallbackText !== undefined"
          class="ds-label-02-reg text-start"
          >{{ fallbackText }}</span
        >
        <Component :is="summaryComponent" v-if="!isExpanded" :data="dataSet" />
      </div>
    </template>

    <div
      class="flex flex-col items-start gap-32"
      :class="{ 'mt-24': isExpanded }"
    >
      <slot />
      <TextButton aria-label="Fertig" label="Fertig" @click="collapse" />
    </div>
  </ExpandableContent>
</template>
