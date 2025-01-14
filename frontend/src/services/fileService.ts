import httpClient, { ServiceResponse } from "./httpClient"
import DocumentUnit from "@/domain/documentUnit"
import errorMessages from "@/shared/i18n/errors.json"

interface FileService {
  upload(
    documentUnitUuid: string,
    file: File,
  ): Promise<ServiceResponse<DocumentUnit>>
  delete(documentUnitUuid: string): Promise<ServiceResponse<unknown>>
  getDocxFileAsHtml(uuid: string): Promise<ServiceResponse<string>>
}

const service: FileService = {
  async upload(documentUnitUuid: string, file: File) {
    const extension = file.name?.split(".").pop()
    if (!extension || extension.toLowerCase() !== "docx") {
      return {
        status: 415,
        error: {
          title: errorMessages.WRONG_MEDIA_TYPE_DOCX_REQUIRED.title,
          description: errorMessages.WRONG_MEDIA_TYPE_DOCX_REQUIRED.description,
        },
      }
    }

    const response = await httpClient.put<File, DocumentUnit>(
      `caselaw/documentunits/${documentUnitUuid}/file`,
      {
        headers: {
          "Content-Type":
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
          "X-Filename": file.name,
        },
      },
      file,
    )
    if (response.status === 413) {
      response.error = {
        title: errorMessages.FILE_TOO_LARGE_CASELAW.title,
        description: errorMessages.FILE_TOO_LARGE_CASELAW.description,
      }
    } else if (response.status === 415) {
      response.error = {
        title: errorMessages.WRONG_MEDIA_TYPE_DOCX_REQUIRED.title,
        description: errorMessages.WRONG_MEDIA_TYPE_DOCX_REQUIRED.description,
      }
    } else if (response.status >= 300) {
      response.error = {
        title: errorMessages.SERVER_ERROR.title,
        description: errorMessages.SERVER_ERROR.description,
      }
    } else {
      response.error = undefined
    }

    return response
  },

  async delete(documentUnitUuid: string) {
    const response = await httpClient.delete(
      `caselaw/documentunits/${documentUnitUuid}/file`,
    )
    response.error =
      response.status >= 300
        ? { title: errorMessages.FILE_DELETE_FAILED.title }
        : undefined

    return response
  },

  async getDocxFileAsHtml(uuid: string) {
    const response = await httpClient.get<string>(
      `caselaw/documentunits/${uuid}/docx`,
    )
    response.error =
      response.status >= 300
        ? { title: errorMessages.DOCX_COULD_NOT_BE_LOADED.title }
        : undefined

    return response
  },
}

export default service
