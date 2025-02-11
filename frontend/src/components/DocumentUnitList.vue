<script lang="ts" setup>
import dayjs from "dayjs"
import { computed, ref } from "vue"
import { DocumentUnitListEntry } from "../domain/documentUnitListEntry"
import { useStatusBadge } from "@/composables/useStatusBadge"
import IconBadge from "@/shared/components/IconBadge.vue"
import PopupModal from "@/shared/components/PopupModal.vue"

const props = defineProps<{
  documentUnitListEntries: DocumentUnitListEntry[]
}>()

const emit = defineEmits<{
  deleteDocumentUnit: [documentUnitListEntry: DocumentUnitListEntry]
}>()

const listEntriesWithStatus = computed(() =>
  props.documentUnitListEntries.map((entry) => ({
    ...entry,
    status: useStatusBadge(entry.status).value,
  })),
)

const showModal = ref(false)
const popupModalText = ref("")
const modalConfirmText = ref("Löschen")
const modalHeaderText = "Dokumentationseinheit löschen"
const modalCancelButtonType = "ghost"
const modalConfirmButtonType = "secondary"
const selectedDocumentUnitListEntry = ref<DocumentUnitListEntry>()
function toggleModal() {
  showModal.value = !showModal.value
  if (showModal.value) {
    const scrollLeft = document.documentElement.scrollLeft
    const scrollTop = document.documentElement.scrollTop
    window.onscroll = () => {
      window.scrollTo(scrollLeft, scrollTop)
    }
  } else {
    window.onscroll = () => {
      return
    }
  }
}
function setSelectedDocumentUnitListEntry(
  documentUnitListEntry: DocumentUnitListEntry,
) {
  selectedDocumentUnitListEntry.value = documentUnitListEntry
  popupModalText.value = `Möchten Sie die Dokumentationseinheit ${selectedDocumentUnitListEntry.value.documentNumber} wirklich dauerhaft löschen?`
  toggleModal()
}
function onDelete() {
  if (selectedDocumentUnitListEntry.value) {
    emit("deleteDocumentUnit", selectedDocumentUnitListEntry.value)
    toggleModal()
  }
}
</script>

<template>
  <div>
    <PopupModal
      v-if="showModal"
      :aria-label="modalHeaderText"
      :cancel-button-type="modalCancelButtonType"
      :confirm-button-type="modalConfirmButtonType"
      :confirm-text="modalConfirmText"
      :content-text="popupModalText"
      :header-text="modalHeaderText"
      @close-modal="toggleModal"
      @confirm-action="onDelete"
    />
    <div
      v-if="documentUnitListEntries.length"
      class="document-unit-list-table table w-full border-collapse"
    >
      <div class="ds-label-01-bold table-row bg-gray-400">
        <div class="table-cell p-16">Dokumentnummer</div>
        <div class="table-cell p-16"></div>
        <div class="table-cell p-16">Gerichtstyp</div>
        <div class="table-cell p-16">Ort</div>
        <div class="table-cell p-16">Datum</div>
        <div class="table-cell p-16">Aktenzeichen</div>
        <div class="table-cell p-16">Typ</div>
        <div class="table-cell p-16">Status</div>
        <div class="table-cell p-16">Löschen</div>
      </div>
      <div
        v-for="listEntry in listEntriesWithStatus"
        :key="listEntry.id"
        class="ds-label-01-reg table-row border-b-2 border-b-gray-100 px-16 hover:bg-gray-100"
      >
        <div class="table-cell p-16">
          <router-link
            class="underline"
            :to="{
              name: listEntry.fileName
                ? 'caselaw-documentUnit-documentNumber-categories'
                : 'caselaw-documentUnit-documentNumber-files',
              params: { documentNumber: listEntry.documentNumber },
            }"
          >
            {{ listEntry.documentNumber }}
          </router-link>
        </div>
        <div class="table-cell p-16">
          <span v-if="listEntry.fileName" class="material-icons">
            attach_file
          </span>
          <span v-else>-</span>
        </div>
        <div class="table-cell p-16">
          {{ listEntry.court?.type ? listEntry.court.type : "-" }}
        </div>
        <div class="table-cell p-16">
          {{ listEntry.court?.location ? listEntry.court.location : "-" }}
        </div>
        <div class="table-cell p-16">
          {{ dayjs(listEntry.creationTimestamp).format("DD.MM.YYYY") }}
        </div>
        <div class="table-cell p-16">
          {{ listEntry.fileNumber ? listEntry.fileNumber : "-" }}
        </div>
        <div class="table-cell p-16">
          {{
            listEntry.documentType ? listEntry.documentType.jurisShortcut : "-"
          }}
        </div>
        <div class="table-cell p-16">
          <IconBadge
            v-if="listEntry.status"
            :color="listEntry.status.color"
            :icon="listEntry.status.icon"
            :value="listEntry.status.value"
          />
        </div>
        <div class="table-cell p-16">
          <span
            aria-label="Dokumentationseinheit löschen"
            class="material-icons cursor-pointer"
            tabindex="0"
            @click="
              setSelectedDocumentUnitListEntry(
                documentUnitListEntries.find(
                  (entry) => entry.uuid == listEntry.uuid,
                ) as DocumentUnitListEntry,
              )
            "
            @keyup.enter="
              setSelectedDocumentUnitListEntry(
                documentUnitListEntries.find(
                  (entry) => entry.uuid == listEntry.uuid,
                ) as DocumentUnitListEntry,
              )
            "
          >
            delete
          </span>
        </div>
      </div>
    </div>
    <span v-else>Keine Dokumentationseinheiten gefunden</span>
  </div>
</template>
