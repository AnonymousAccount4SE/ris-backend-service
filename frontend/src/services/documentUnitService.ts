import DocumentUnit, { ProceedingDecision } from "../domain/documentUnit"
import { DocumentUnitListEntry } from "../domain/documentUnitListEntry"
import httpClient, {
  ServiceResponse,
  FailedValidationServerResponse,
} from "./httpClient"
import { PageableService, Page } from "@/shared/components/Pagination.vue"

interface DocumentUnitService {
  getAllListEntries: PageableService<DocumentUnitListEntry>
  getByDocumentNumber(
    documentNumber: string
  ): Promise<ServiceResponse<DocumentUnit>>
  createNew(
    docCenter: string,
    docType: string
  ): Promise<ServiceResponse<DocumentUnit>>
  update(documentUnit: DocumentUnit): Promise<ServiceResponse<unknown>>
  delete(documentUnitUuid: string): Promise<ServiceResponse<unknown>>
  searchByProceedingDecisionInput(
    proceedingDecision: ProceedingDecision
  ): Promise<ServiceResponse<ProceedingDecision[]>>
}

const service: DocumentUnitService = {
  async getAllListEntries(page: number, size: number) {
    const response = await httpClient.get<Page<DocumentUnitListEntry>>(
      `caselaw/documentunits?pg=${page}&sz=${size}`
    )
    if (response.status >= 300) {
      response.error = {
        title: "Dokumentationseinheiten konnten nicht geladen werden.",
      }
    }
    return response
  },

  async getByDocumentNumber(documentNumber: string) {
    const response = await httpClient.get<DocumentUnit>(
      `caselaw/documentunits/${documentNumber}`
    )
    if (response.status >= 300 || response.error) {
      response.error = {
        title: "Dokumentationseinheit konnten nicht geladen werden.",
      }
    } else {
      response.data = new DocumentUnit(response.data.uuid, { ...response.data })
    }
    return response
  },

  async createNew(docCenter: string, docType: string) {
    const response = await httpClient.post<Partial<DocumentUnit>, DocumentUnit>(
      "caselaw/documentunits",
      {
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
      },
      JSON.stringify({
        documentationCenterAbbreviation: docCenter,
        documentType: docType,
      }) as Partial<DocumentUnit>
    )
    if (response.status >= 300) {
      response.error = {
        title: "Neue Dokumentationseinheit konnte nicht erstellt werden.",
      }
    } else {
      response.data = new DocumentUnit((response.data as DocumentUnit).uuid, {
        ...(response.data as DocumentUnit),
      })
    }
    return response
  },

  async update(documentUnit: DocumentUnit) {
    const response = await httpClient.put<
      DocumentUnit,
      DocumentUnit | FailedValidationServerResponse
    >(
      `caselaw/documentunits/${documentUnit.uuid}`,
      {
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
      },
      documentUnit
    )
    if (response.status >= 300) {
      response.error = {
        title: "Dokumentationseinheit konnte nicht aktualisiert werden.",
      }
      // good enough condition to detect validation errors (@Valid)?
      if (
        response.status == 400 &&
        JSON.stringify(response.data).includes("Validation failed")
      ) {
        response.error.validationErrors = (
          response.data as FailedValidationServerResponse
        ).errors
      }
    } else {
      response.data = new DocumentUnit((response.data as DocumentUnit).uuid, {
        ...(response.data as DocumentUnit),
      })
    }
    return response
  },

  async delete(documentUnitUuid: string) {
    const response = await httpClient.delete(
      `caselaw/documentunits/${documentUnitUuid}`
    )
    if (response.status >= 300) {
      response.error = {
        title: "Dokumentationseinheit konnte nicht gelöscht werden.",
      }
    }
    return response
  },

  async searchByProceedingDecisionInput(
    proceedingDecision: ProceedingDecision
  ) {
    const response = await httpClient.put<
      ProceedingDecision,
      ProceedingDecision[]
    >(
      `caselaw/documentunits/search`,
      {
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
      },
      proceedingDecision
    )
    if (response.status >= 300) {
      response.error = {
        title: `Die Suche nach passenden Dokumentationseinheit konnte nicht ausgeführt werden`,
      }
    }
    return {
      status: 200,
      data: (response.data as ProceedingDecision[]).map(
        (decision) => new ProceedingDecision({ ...decision })
      ),
    }
  },
}

export default service
