package de.bund.digitalservice.ris.caselaw.adapter.converter.docx;

import de.bund.digitalservice.ris.caselaw.domain.docx.BorderNumber;
import de.bund.digitalservice.ris.caselaw.domain.docx.DocumentUnitDocx;
import de.bund.digitalservice.ris.caselaw.domain.docx.NumberingList;
import de.bund.digitalservice.ris.caselaw.domain.docx.NumberingListEntry;
import de.bund.digitalservice.ris.caselaw.domain.docx.ParagraphElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentUnitDocxListUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentUnitDocxListUtils.class);

  private DocumentUnitDocxListUtils() {}

  public static void postProcessBorderNumbers(List<DocumentUnitDocx> documentUnitDocxList) {
    if (documentUnitDocxList == null) {
      return;
    }
    Integer numIdOfCurrentBorderNumberBlock = null;
    int borderNumberCounter = 1;

    for (DocumentUnitDocx documentUnitDocx : documentUnitDocxList) {
      if (documentUnitDocx instanceof BorderNumber borderNumber
          && borderNumber.getNumber().isEmpty()) {
        borderNumber.addNumberText(String.valueOf(borderNumberCounter++));
        if (numIdOfCurrentBorderNumberBlock != null
            && borderNumber.getNumId() != null
            && !borderNumber.getNumId().equals(numIdOfCurrentBorderNumberBlock)) {
          LOGGER.error(
              "Unexpected case of a new numId. Are there more than one border number blocks "
                  + "in this document? Then we need to support this case. Until then "
                  + "every border number block after the first one will not start at 1. Instead"
                  + "it is a continuous counting up across the whole document. ");
        }
        numIdOfCurrentBorderNumberBlock = borderNumber.getNumId();
      }
    }
  }

  public static List<DocumentUnitDocx> packList(List<DocumentUnitDocx> unpackedList) {
    if (unpackedList == null) {
      return Collections.emptyList();
    }

    AtomicReference<BorderNumber> finalBorderNumber = new AtomicReference<>();

    List<DocumentUnitDocx> mergedList = firstRun(unpackedList, finalBorderNumber);
    return secondRun(mergedList, finalBorderNumber);
  }

  /**
   * first run through: - pack consecutive NumberingListEntry's into one NumberingList element per
   * block, build a new list --> mergedList - identify last BorderNumber
   *
   * @param unpackedList list of unpacked elements
   * @return merged list of document unit docx objects
   */
  private static List<DocumentUnitDocx> firstRun(
      List<DocumentUnitDocx> unpackedList, AtomicReference<BorderNumber> finalBorderNumber) {

    List<DocumentUnitDocx> mergedList = new ArrayList<>();

    NumberingList currentNumberingList = null;

    for (DocumentUnitDocx element : unpackedList) {
      if (element instanceof NumberingListEntry numberingListEntry) {
        if (currentNumberingList == null) {
          currentNumberingList = new NumberingList();
        }
        currentNumberingList.addNumberingListEntry(numberingListEntry);
      } else {
        if (currentNumberingList != null) {
          mergedList.add(currentNumberingList);
        }
        currentNumberingList = null;
        mergedList.add(element);
      }
      if (element instanceof BorderNumber borderNumber) {
        finalBorderNumber.set(borderNumber);
      }
    }
    if (currentNumberingList != null) {
      mergedList.add(currentNumberingList);
    }

    return mergedList;
  }

  /**
   * second run through: - All elements between BorderNumber's get attached to the BorderNumber on
   * top. Two conditions can break this rule: a paragraph that is centered or if it's the last
   * BorderNumber in the document. - If no BorderNumber is active as current parent, the element
   * goes into the packedList without a parent.
   *
   * @param mergedList - result of the first run
   * @return packed list of documentation unit docx objects
   */
  private static List<DocumentUnitDocx> secondRun(
      List<DocumentUnitDocx> mergedList, AtomicReference<BorderNumber> finalBorderNumber) {

    List<DocumentUnitDocx> packedList = new ArrayList<>();

    BorderNumber currentBorderNumber = null;

    for (DocumentUnitDocx element : mergedList) {
      // if we encounter a BorderNumber, this is the new parent of all following elements
      // until a new BorderNumber or a BorderNumber-block-breaking condition comes along
      if (element instanceof BorderNumber borderNumber) {
        currentBorderNumber = borderNumber;
        packedList.add(element);
        continue;
      }
      // BorderNumber-block-breaking condition 1: centered paragraphs
      if (element instanceof ParagraphElement paragraphElement
          && paragraphElement.getStyleString().contains("text-align: center;")) {
        currentBorderNumber = null;
      }
      // BorderNumber-block-breaking condition 2: the final BorderNumber of the whole
      // document (as scanned for above) only gets one child attached (otherwise
      // the whole rest of the document would get absorbed into the final BorderNumber)
      if (currentBorderNumber != null
          && currentBorderNumber == finalBorderNumber.get()
          && currentBorderNumber.getChildrenSize() >= 1) {
        currentBorderNumber = null;
      }
      if (currentBorderNumber == null) {
        // not within a BorderNumber-block --> add whatever it is straight to the list
        packedList.add(element);
      } else {
        // in a BorderNumber-block and no breaking condition was encountered
        // --> add this element as child of the current BorderNumber
        currentBorderNumber.addChild(element);
      }
    }

    return packedList;
  }
}
