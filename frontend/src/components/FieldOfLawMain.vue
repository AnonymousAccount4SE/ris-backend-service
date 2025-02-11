<script lang="ts" setup>
import { h, ref } from "vue"
import FieldOfLawSelectionList from "./FieldOfLawSelectionList.vue"
import FieldOfLawTree from "./FieldOfLawTree.vue"
import ExpandableDataSet from "@/components/ExpandableDataSet.vue"
import FieldOfLawDirectInputSearch from "@/components/FieldOfLawDirectInputSearch.vue"
import FieldOfLawSearch from "@/components/FieldOfLawSearch.vue"
import { FieldOfLawNode } from "@/domain/fieldOfLaw"
import FieldOfLawService from "@/services/fieldOfLawService"
import { withSummarizer } from "@/shared/components/DataSetSummary.vue"

const props = defineProps<{
  documentUnitUuid: string
}>()

const selectedFieldsOfLaw = ref<FieldOfLawNode[]>([])
const clickedIdentifier = ref("")
const showNorms = ref(false)

const response = await FieldOfLawService.getSelectedFieldsOfLaw(
  props.documentUnitUuid,
)

if (response.data) {
  selectedFieldsOfLaw.value = response.data
}

const handleAdd = async (identifier: string) => {
  const response = await FieldOfLawService.addFieldOfLaw(
    props.documentUnitUuid,
    identifier,
  )

  if (response.data) {
    selectedFieldsOfLaw.value = response.data
  }
}

const handleRemoveByIdentifier = async (identifier: string) => {
  const response = await FieldOfLawService.removeFieldOfLaw(
    props.documentUnitUuid,
    identifier,
  )

  if (response.data) {
    selectedFieldsOfLaw.value = response.data
  }
}

function handleNodeClicked(identifier: string) {
  clickedIdentifier.value = identifier
}

function handleResetClickedNode() {
  clickedIdentifier.value = ""
}

function handleLinkedFieldClicked(identifier: string) {
  clickedIdentifier.value = identifier
}

function handleIdentifierClickInSummary(identifier: string) {
  setTimeout(() => {
    clickedIdentifier.value = identifier
  }, 20)
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function selectedFieldsOfLawSummarizer(dataEntry: any) {
  return h("div", [
    h(
      "span",
      {
        class: "text-blue-800",
        onClick: () => handleIdentifierClickInSummary(dataEntry.identifier),
      },
      dataEntry.identifier,
    ),
    ", " + dataEntry.text,
  ])
}

const SelectedFieldsOfLawSummary = withSummarizer(selectedFieldsOfLawSummarizer)
</script>

<template>
  <ExpandableDataSet
    as-column
    :data-set="selectedFieldsOfLaw"
    :summary-component="SelectedFieldsOfLawSummary"
    title="Sachgebiete"
  >
    <div class="w-full">
      <div class="flex flex-row">
        <div class="flex flex-1 flex-col bg-white p-20">
          <FieldOfLawSearch
            :show-norms="showNorms"
            @do-show-norms="showNorms = true"
            @node-clicked="handleNodeClicked"
          />
        </div>
        <div class="flex-1 bg-white p-20">
          <FieldOfLawTree
            :clicked-identifier="clickedIdentifier"
            :selected-nodes="selectedFieldsOfLaw"
            :show-norms="showNorms"
            @add-to-list="handleAdd"
            @linked-field:clicked="handleLinkedFieldClicked"
            @remove-from-list="handleRemoveByIdentifier"
            @reset-clicked-node="handleResetClickedNode"
            @toggle-show-norms="showNorms = !showNorms"
          ></FieldOfLawTree>
        </div>
      </div>
      <hr class="w-full border-blue-700" />
      <div class="bg-white p-20">
        <h1 class="ds-heading-03-reg pb-8">Ausgewählte Sachgebiete</h1>
        <FieldOfLawDirectInputSearch @add-to-list="handleAdd" />
        <FieldOfLawSelectionList
          :selected-fields-of-law="selectedFieldsOfLaw"
          @linked-field:clicked="handleLinkedFieldClicked"
          @node-clicked="handleNodeClicked"
          @remove-from-list="handleRemoveByIdentifier"
        ></FieldOfLawSelectionList>
      </div>
    </div>
  </ExpandableDataSet>
</template>
