UPDATE
  doc_unit
SET
  document_type_id = (
    SELECT
      id
    FROM
      lookuptable_documenttype
    WHERE
      lookuptable_documenttype.label = doc_unit.dokumenttyp
    LIMIT
      1
  );