<script lang="ts" setup>
import { ref } from "vue"
import DocumentUnitWrapper from "@/components/DocumentUnitWrapper.vue"
import FileViewer from "@/components/FileViewer.vue"
import DocumentUnit from "@/domain/documentUnit"
import documentUnitService from "@/services/documentUnitService"
import fileService from "@/services/fileService"
import { ResponseError } from "@/services/httpClient"
import FileUpload from "@/shared/components/FileUpload.vue"

const props = defineProps<{ documentUnit: DocumentUnit }>()
const emit = defineEmits<{
  updateDocumentUnit: [updatedDocumentUnit: DocumentUnit]
}>()
const error = ref<ResponseError>()
const isUploading = ref(false)

async function handleDeleteFile() {
  if ((await fileService.delete(props.documentUnit.uuid)).status < 300) {
    const updateResponse = await documentUnitService.getByDocumentNumber(
      props.documentUnit.documentNumber as string,
    )
    if (updateResponse.error) {
      console.error(updateResponse.error)
    } else {
      emit("updateDocumentUnit", updateResponse.data)
    }
  }
}

async function upload(file: File) {
  isUploading.value = true

  try {
    const response = await fileService.upload(props.documentUnit.uuid, file)
    if (response.status === 201 && response.data) {
      emit("updateDocumentUnit", response.data)
    } else {
      error.value = response.error
    }
  } finally {
    isUploading.value = false
  }
}
</script>

<template>
  <DocumentUnitWrapper :document-unit="documentUnit">
    <template #default="{ classes }">
      <div class="flex flex-col" :class="classes">
        <h1 class="ds-heading-02-reg mb-[1rem]">Dokumente</h1>

        <FileViewer
          v-if="documentUnit.hasFile"
          :file-name="documentUnit.filename"
          :file-type="documentUnit.filetype"
          :upload-time-stamp="documentUnit.fileuploadtimestamp"
          :uuid="documentUnit.uuid"
          @delete-file="handleDeleteFile"
        />

        <div v-else class="flex w-[40rem] flex-col items-start">
          <div class="mb-14">
            Aktuell ist keine Datei hinterlegt. Wählen Sie die Datei des
            Originaldokumentes aus
          </div>

          <FileUpload
            :error="error"
            :is-loading="isUploading"
            @file-selected="(file) => upload(file)"
          />
        </div>
      </div>
    </template>
  </DocumentUnitWrapper>
</template>
