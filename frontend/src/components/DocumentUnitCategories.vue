<script setup lang="ts">
import { computed, ref, onMounted, toRefs, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import DocumentUnitContentRelatedIndexing from "@/components/DocumentUnitContentRelatedIndexing.vue"
import DocumentUnitCoreData from "@/components/DocumentUnitCoreData.vue"
import DocumentUnitTexts from "@/components/DocumentUnitTexts.vue"
import DocumentUnitWrapper from "@/components/DocumentUnitWrapper.vue"
import OriginalFileSidePanel from "@/components/OriginalFileSidePanel.vue"
import DocumentUnitProceedingDecision from "@/components/proceedingDecisions/ProceedingDecisions.vue"
import SaveButton from "@/components/SaveDocumentUnitButton.vue"
import { useScrollToHash } from "@/composables/useScrollToHash"
import { useToggleStateInRouteQuery } from "@/composables/useToggleStateInRouteQuery"
import DocumentUnit, { Texts } from "@/domain/documentUnit"
import ProceedingDecision from "@/domain/proceedingDecision"
import documentUnitService from "@/services/documentUnitService"
import fileService from "@/services/fileService"
import { ServiceResponse } from "@/services/httpClient"
import { ValidationError } from "@/shared/components/input/types"
import SideToggle, {
  OpeningDirection,
} from "@/shared/components/SideToggle.vue"

const props = defineProps<{
  documentUnit: DocumentUnit
}>()
const updatedDocumentUnit = ref<DocumentUnit>(props.documentUnit)
const validationErrors = ref<ValidationError[]>([])
const router = useRouter()
const route = useRoute()
const fileAsHTML = ref("")
const showDocPanel = useToggleStateInRouteQuery(
  "showDocPanel",
  route,
  router.replace,
  false,
)
const hasDataChange = ref(false)
const lastUpdatedDocumentUnit = ref()

const handleUpdateValueDocumentUnitTexts = async (
  updatedValue: [keyof Texts, string],
) => {
  const divElem = document.createElement("div")
  divElem.innerHTML = updatedValue[1]
  const hasImgElem = divElem.getElementsByTagName("img").length > 0
  const hasTable = divElem.getElementsByTagName("table").length > 0
  const hasInnerText = divElem.innerText.length > 0
  updatedDocumentUnit.value.texts[updatedValue[0]] =
    hasInnerText || hasImgElem || hasTable ? updatedValue[1] : ""
}

async function handleUpdateDocumentUnit(): Promise<ServiceResponse<void>> {
  hasDataChange.value =
    JSON.stringify(updatedDocumentUnit.value)
      .replaceAll('"norms":[{"validationError":false}]', '"norms":[]')
      .replaceAll('"activeCitations":[{}]', '"activeCitations":[]') !==
    JSON.stringify(lastUpdatedDocumentUnit.value)

  if (hasDataChange.value) {
    const response = await documentUnitService.update(
      updatedDocumentUnit.value as DocumentUnit,
    )
    if (response?.error?.validationErrors) {
      validationErrors.value = response.error.validationErrors
    } else {
      validationErrors.value = []
    }
    if (response.data) {
      updatedDocumentUnit.value = response.data as DocumentUnit
    }
    lastUpdatedDocumentUnit.value = JSON.parse(
      JSON.stringify(updatedDocumentUnit.value),
    )

    hasDataChange.value = false
    return response as ServiceResponse<void>
  }
  return { status: 200, data: undefined } as ServiceResponse<void>
}

watch(
  showDocPanel,
  async () => {
    if (showDocPanel.value && fileAsHTML.value.length == 0) {
      await getOriginalDocumentUnit()
    }
  },
  { immediate: true },
)

const coreData = computed({
  get: () => updatedDocumentUnit.value.coreData,
  set: (newValues) => {
    let triggerSaving = false
    if (
      updatedDocumentUnit.value.coreData.court?.label !== newValues.court?.label
    ) {
      triggerSaving = true
    }
    Object.assign(updatedDocumentUnit.value.coreData, newValues)
    if (triggerSaving) {
      handleUpdateDocumentUnit()
    }
  },
})

const proceedingDecisions = computed({
  get: () =>
    updatedDocumentUnit.value.proceedingDecisions as ProceedingDecision[],
  set: (newValues) => {
    updatedDocumentUnit.value.proceedingDecisions = newValues
  },
})

const contentRelatedIndexing = computed({
  get: () => (updatedDocumentUnit.value as DocumentUnit).contentRelatedIndexing,
  set: (newValues) => {
    Object.assign(updatedDocumentUnit.value.contentRelatedIndexing, newValues)
  },
})

const { hash: routeHash } = toRefs(route)
useScrollToHash(routeHash)

async function getOriginalDocumentUnit() {
  if (fileAsHTML.value.length > 0) return
  if (props.documentUnit.s3path) {
    const htmlResponse = await fileService.getDocxFileAsHtml(
      props.documentUnit.uuid,
    )
    if (htmlResponse.error === undefined) fileAsHTML.value = htmlResponse.data
  }
}

onMounted(async () => {
  await getOriginalDocumentUnit()
})
</script>

<template>
  <DocumentUnitWrapper :document-unit="updatedDocumentUnit as DocumentUnit">
    <template #default="{ classes }">
      <div
        class="sticky top-[2rem] z-30 -mt-96 grid h-[6rem] w-full justify-items-end pb-8 pr-[2rem]"
      >
        <SaveButton
          aria-label="Speichern Button"
          :service-callback="handleUpdateDocumentUnit"
        />
      </div>

      <div class="flex w-full flex-grow">
        <div class="flex flex-col bg-gray-100" :class="classes">
          <DocumentUnitCoreData
            id="coreData"
            v-model="coreData"
            :validation-errors="
              validationErrors.filter(
                (err) => err.field.split('\.')[0] === 'coreData',
              )
            "
          />

          <DocumentUnitProceedingDecision v-model="proceedingDecisions" />

          <DocumentUnitContentRelatedIndexing
            id="contentRelatedIndexing"
            v-model="contentRelatedIndexing"
            :document-unit-uuid="updatedDocumentUnit.uuid"
          />

          <DocumentUnitTexts
            id="texts"
            :texts="documentUnit.texts"
            @update-value="handleUpdateValueDocumentUnitTexts"
          />
        </div>

        <div
          class="flex flex-col border-l-1 border-solid border-gray-400 bg-white"
          :class="{ full: showDocPanel }"
        >
          <SideToggle
            v-model:is-expanded="showDocPanel"
            class="sticky top-[8rem] z-20"
            label="Originaldokument"
            :opening-direction="OpeningDirection.LEFT"
          >
            <OriginalFileSidePanel
              id="odoc-panel-element"
              v-model:open="showDocPanel"
              class="bg-white"
              :class="classes"
              :file="fileAsHTML"
              :has-file="documentUnit.hasFile"
            />
          </SideToggle>
        </div>
      </div>
    </template>
  </DocumentUnitWrapper>
</template>

<style lang="scss" scoped>
.full {
  @apply w-full grow;
}
</style>
