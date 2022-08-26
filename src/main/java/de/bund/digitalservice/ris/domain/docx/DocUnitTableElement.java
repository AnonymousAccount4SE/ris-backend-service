package de.bund.digitalservice.ris.domain.docx;

import java.util.List;
import java.util.stream.Collectors;

public class DocUnitTableElement extends BlockElement implements DocUnitDocx {
  public final List<DocUnitTableRowElement> rows;

  public DocUnitTableElement(List<DocUnitTableRowElement> rows) {
    this.rows = rows;
  }

  @Override
  public String toHtmlString() {
    return "<table style=\"border-collapse: collapse;"
        + borderToHtmlString()
        + backgroundColorToHtmlString()
        + "\">"
        + rows.stream().map(DocUnitTableRowElement::toHtmlString).collect(Collectors.joining())
        + "</table>";
  }
}
