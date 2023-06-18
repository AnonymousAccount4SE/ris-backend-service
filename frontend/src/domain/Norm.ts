export interface Norm extends NormBase, FlatMetadata {
  metadataSections?: MetadataSections
}

export interface NormBase {
  readonly guid: string
  articles: Article[]
  readonly files?: FileReference[]
}

export type Article = {
  guid: string
  title: string
  marker: string
  readonly paragraphs: Paragraph[]
}

export type Paragraph = {
  guid: string
  marker: string
  text: string
}

export interface FileReference {
  readonly name: string
  readonly hash: string
  readonly createdAt: string
}

export enum MetadatumType {
  KEYWORD = "KEYWORD",
  UNOFFICIAL_LONG_TITLE = "UNOFFICIAL_LONG_TITLE",
  UNOFFICIAL_SHORT_TITLE = "UNOFFICIAL_SHORT_TITLE",
  UNOFFICIAL_ABBREVIATION = "UNOFFICIAL_ABBREVIATION",
  UNOFFICIAL_REFERENCE = "UNOFFICIAL_REFERENCE",
  DIVERGENT_DOCUMENT_NUMBER = "DIVERGENT_DOCUMENT_NUMBER",
  REFERENCE_NUMBER = "REFERENCE_NUMBER",
  DEFINITION = "DEFINITION",
  RIS_ABBREVIATION_INTERNATIONAL_LAW = "RIS_ABBREVIATION_INTERNATIONAL_LAW",
  AGE_OF_MAJORITY_INDICATION = "AGE_OF_MAJORITY_INDICATION",
  VALIDITY_RULE = "VALIDITY_RULE",
  LEAD_JURISDICTION = "LEAD_JURISDICTION",
  LEAD_UNIT = "LEAD_UNIT",
  PARTICIPATION_TYPE = "PARTICIPATION_TYPE",
  PARTICIPATION_INSTITUTION = "PARTICIPATION_INSTITUTION",
  SUBJECT_FNA = "SUBJECT_FNA",
  SUBJECT_PREVIOUS_FNA = "SUBJECT_PREVIOUS_FNA",
  SUBJECT_GESTA = "SUBJECT_GESTA",
  SUBJECT_BGB_3 = "SUBJECT_BGB_3",
  YEAR = "YEAR",
  DATE = "DATE",
  RANGE_START = "RANGE_START",
  RANGE_END = "RANGE_END",
  ANNOUNCEMENT_MEDIUM = "ANNOUNCEMENT_MEDIUM",
  ANNOUNCEMENT_GAZETTE = "ANNOUNCEMENT_GAZETTE",
  ADDITIONAL_INFO = "ADDITIONAL_INFO",
  EXPLANATION = "EXPLANATION",
  AREA_OF_PUBLICATION = "AREA_OF_PUBLICATION",
  NUMBER_OF_THE_PUBLICATION_IN_THE_RESPECTIVE_AREA = "NUMBER_OF_THE_PUBLICATION_IN_THE_RESPECTIVE_AREA",
  EU_GOVERNMENT_GAZETTE = "EU_GOVERNMENT_GAZETTE",
  SERIES = "SERIES",
  OTHER_OFFICIAL_REFERENCE = "OTHER_OFFICIAL_REFERENCE",
  NUMBER = "NUMBER",
  PAGE = "PAGE",
  EDITION = "EDITION",
  ENTITY = "ENTITY",
  DECIDING_BODY = "DECIDING_BODY",
  RESOLUTION_MAJORITY = "RESOLUTION_MAJORITY",
  TYPE_NAME = "TYPE_NAME",
  NORM_CATEGORY = "NORM_CATEGORY",
  TEMPLATE_NAME = "TEMPLATE_NAME",
  UNDEFINED_DATE = "UNDEFINED_DATE",
  TEXT = "TEXT",
  LINK = "LINK",
  RELATED_DATA = "RELATED_DATA",
  EXTERNAL_DATA_NOTE = "EXTERNAL_DATA_NOTE",
  APPENDIX = "APPENDIX",
  FOOTNOTE_REFERENCE = "FOOTNOTE_REFERENCE",
  FOOTNOTE_CHANGE = "FOOTNOTE_CHANGE",
  FOOTNOTE_COMMENT = "FOOTNOTE_COMMENT",
  FOOTNOTE_DECISION = "FOOTNOTE_DECISION",
  FOOTNOTE_STATE_LAW = "FOOTNOTE_STATE_LAW",
  FOOTNOTE_EU_LAW = "FOOTNOTE_EU_LAW",
  FOOTNOTE_OTHER = "FOOTNOTE_OTHER",
  WORK_NOTE = "WORK_NOTE",
  DESCRIPTION = "DESCRIPTION",
  REFERENCE = "REFERENCE",
  ENTRY_INTO_FORCE_DATE_NOTE = "ENTRY_INTO_FORCE_DATE_NOTE",
  PROOF_INDICATION = "PROOF_INDICATION",
  PROOF_TYPE = "PROOF_TYPE",
  OTHER_TYPE = "OTHER_TYPE",
}

export enum NormCategory {
  AMENDMENT_NORM = "AMENDMENT_NORM",
  BASE_NORM = "BASE_NORM",
  TRANSITIONAL_NORM = "TRANSITIONAL_NORM",
}

export enum ProofIndication {
  NOT_YET_CONSIDERED = "NOT_YET_CONSIDERED",
  CONSIDERED = "CONSIDERED",
}

export enum ProofType {
  TEXT_PROOF_FROM = "TEXT_PROOF_FROM",
  TEXT_PROOF_VALIDITY_FROM = "TEXT_PROOF_VALIDITY_FROM",
}

export enum OtherType {
  TEXT_IN_PROGRESS = "TEXT_IN_PROGRESS",
  TEXT_PROOFED_BUT_NOT_DONE = "TEXT_PROOFED_BUT_NOT_DONE",
}

// TODO: Establish typing that requires all `MetadatumType`s to be listed.
export type MetadataValueType = {
  [MetadatumType.KEYWORD]: string
  [MetadatumType.UNOFFICIAL_LONG_TITLE]: string
  [MetadatumType.UNOFFICIAL_SHORT_TITLE]: string
  [MetadatumType.UNOFFICIAL_ABBREVIATION]: string
  [MetadatumType.UNOFFICIAL_REFERENCE]: string
  [MetadatumType.DIVERGENT_DOCUMENT_NUMBER]: string
  [MetadatumType.REFERENCE_NUMBER]: string
  [MetadatumType.DEFINITION]: string
  [MetadatumType.RIS_ABBREVIATION_INTERNATIONAL_LAW]: string
  [MetadatumType.AGE_OF_MAJORITY_INDICATION]: string
  [MetadatumType.VALIDITY_RULE]: string
  [MetadatumType.LEAD_JURISDICTION]: string
  [MetadatumType.LEAD_UNIT]: string
  [MetadatumType.PARTICIPATION_TYPE]: string
  [MetadatumType.PARTICIPATION_INSTITUTION]: string
  [MetadatumType.SUBJECT_FNA]: string
  [MetadatumType.SUBJECT_PREVIOUS_FNA]: string
  [MetadatumType.SUBJECT_GESTA]: string
  [MetadatumType.SUBJECT_BGB_3]: string
  [MetadatumType.DATE]: string
  [MetadatumType.YEAR]: string
  [MetadatumType.RANGE_START]: string
  [MetadatumType.RANGE_END]: string
  [MetadatumType.ANNOUNCEMENT_MEDIUM]: string
  [MetadatumType.ANNOUNCEMENT_GAZETTE]: string
  [MetadatumType.ADDITIONAL_INFO]: string
  [MetadatumType.EXPLANATION]: string
  [MetadatumType.AREA_OF_PUBLICATION]: string
  [MetadatumType.NUMBER_OF_THE_PUBLICATION_IN_THE_RESPECTIVE_AREA]: string
  [MetadatumType.EU_GOVERNMENT_GAZETTE]: string
  [MetadatumType.SERIES]: string
  [MetadatumType.OTHER_OFFICIAL_REFERENCE]: string
  [MetadatumType.NUMBER]: string
  [MetadatumType.PAGE]: string
  [MetadatumType.EDITION]: string
  [MetadatumType.ENTITY]: string
  [MetadatumType.DECIDING_BODY]: string
  [MetadatumType.RESOLUTION_MAJORITY]: boolean
  [MetadatumType.TYPE_NAME]: string
  [MetadatumType.NORM_CATEGORY]: NormCategory
  [MetadatumType.TEMPLATE_NAME]: string
  [MetadatumType.UNDEFINED_DATE]: UndefinedDate
  [MetadatumType.TEXT]: string
  [MetadatumType.LINK]: string
  [MetadatumType.RELATED_DATA]: string
  [MetadatumType.EXTERNAL_DATA_NOTE]: string
  [MetadatumType.APPENDIX]: string
  [MetadatumType.FOOTNOTE_REFERENCE]: string
  [MetadatumType.FOOTNOTE_CHANGE]: string
  [MetadatumType.FOOTNOTE_COMMENT]: string
  [MetadatumType.FOOTNOTE_DECISION]: string
  [MetadatumType.FOOTNOTE_STATE_LAW]: string
  [MetadatumType.FOOTNOTE_EU_LAW]: string
  [MetadatumType.FOOTNOTE_OTHER]: string
  [MetadatumType.WORK_NOTE]: string
  [MetadatumType.DESCRIPTION]: string
  [MetadatumType.REFERENCE]: string
  [MetadatumType.ENTRY_INTO_FORCE_DATE_NOTE]: string
  [MetadatumType.PROOF_INDICATION]: ProofIndication
  [MetadatumType.PROOF_TYPE]: ProofType
  [MetadatumType.OTHER_TYPE]: OtherType
}

export type Metadata = {
  [Type in MetadatumType]?: MetadataValueType[Type][] | undefined
}

export enum MetadataSectionName {
  NORM = "NORM",
  SUBJECT_AREA = "SUBJECT_AREA",
  LEAD = "LEAD",
  PARTICIPATION = "PARTICIPATION",
  CITATION_DATE = "CITATION_DATE",
  AGE_INDICATION = "AGE_INDICATION",
  DIGITAL_ANNOUNCEMENT = "DIGITAL_ANNOUNCEMENT",
  PRINT_ANNOUNCEMENT = "PRINT_ANNOUNCEMENT",
  EU_ANNOUNCEMENT = "EU_ANNOUNCEMENT",
  OTHER_OFFICIAL_ANNOUNCEMENT = "OTHER_OFFICIAL_ANNOUNCEMENT",
  NORM_PROVIDER = "NORM_PROVIDER",
  OFFICIAL_REFERENCE = "OFFICIAL_REFERENCE",
  DOCUMENT_TYPE = "DOCUMENT_TYPE",
  DIVERGENT_ENTRY_INTO_FORCE = "DIVERGENT_ENTRY_INTO_FORCE",
  DIVERGENT_ENTRY_INTO_FORCE_DEFINED = "DIVERGENT_ENTRY_INTO_FORCE_DEFINED",
  DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED = "DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED",
  DIVERGENT_EXPIRATION = "DIVERGENT_EXPIRATION",
  DIVERGENT_EXPIRATION_DEFINED = "DIVERGENT_EXPIRATION_DEFINED",
  DIVERGENT_EXPIRATION_UNDEFINED = "DIVERGENT_EXPIRATION_UNDEFINED",
  CATEGORIZED_REFERENCE = "CATEGORIZED_REFERENCE",
  ENTRY_INTO_FORCE = "ENTRY_INTO_FORCE",
  PRINCIPLE_ENTRY_INTO_FORCE = "PRINCIPLE_ENTRY_INTO_FORCE",
  EXPIRATION = "EXPIRATION",
  PRINCIPLE_EXPIRATION = "PRINCIPLE_EXPIRATION",
  DIGITAL_EVIDENCE = "DIGITAL_EVIDENCE",
  FOOTNOTES = "FOOTNOTES",
  DOCUMENT_STATUS_SECTION = "DOCUMENT_STATUS_SECTION",
  DOCUMENT_STATUS = "DOCUMENT_STATUS",
  DOCUMENT_TEXT_PROOF = "DOCUMENT_TEXT_PROOF",
  DOCUMENT_OTHER = "DOCUMENT_OTHER",
}

export type MetadataSections = {
  [Name in MetadataSectionName]?: (Metadata & MetadataSections)[] | undefined
}

export type FlatMetadata = {
  applicationScopeArea?: string
  applicationScopeEndDate?: string
  applicationScopeStartDate?: string
  categorizedReference?: string
  celexNumber?: string
  completeCitation?: string
  documentCategory?: string
  documentNumber?: string
  documentStatusDate?: string
  documentStatusDescription?: string
  documentStatusEntryIntoForceDate?: string
  documentStatusProof?: string
  documentStatusReference?: string
  documentStatusWorkNote?: string
  documentTextProof?: string
  eli?: string
  officialAbbreviation?: string
  officialLongTitle: string
  officialShortTitle?: string
  otherDocumentNote?: string
  otherFootnote?: string
  footnoteChange?: string
  footnoteComment?: string
  footnoteDecision?: string
  footnoteStateLaw?: string
  footnoteEuLaw?: string
  otherStatusNote?: string
  printAnnouncementGazette?: string
  printAnnouncementPage?: string
  printAnnouncementYear?: string
  announcementDate?: string
  publicationDate?: string
  reissueArticle?: string
  reissueDate?: string
  reissueNote?: string
  reissueReference?: string
  repealArticle?: string
  repealDate?: string
  repealNote?: string
  repealReferences?: string
  risAbbreviation?: string
  statusDate?: string
  statusDescription?: string
  statusNote?: string
  statusReference?: string
  text?: string
}

export enum UndefinedDate {
  UNDEFINED_UNKNOWN = "UNDEFINED_UNKNOWN",
  UNDEFINED_FUTURE = "UNDEFINED_FUTURE",
  UNDEFINED_NOT_PRESENT = "UNDEFINED_NOT_PRESENT",
}
