package com.sipgate.sparta.diameter.specextractor._3gpp;

import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpFlagRule;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DocxAvpTableReaderTest {

    private static final String CAPTION = "Table 6.3.1: Cx specific Diameter AVPs";
    private static final long VENDOR_ID = 10415L;

    @Test
    void it_parses_a_row_whose_AVP_Code_cell_carries_a_NOTE_annotation() throws IOException {
        // GIVEN a docx with a captioned AVP table where one row has "104" and "NOTE 3" as two paragraphs in the code cell
        final byte[] docx = buildDocxWithDigestRealmRow();

        // WHEN the reader parses it
        final List<AvpDef> defs;
        try (final InputStream in = new ByteArrayInputStream(docx)) {
            defs = DocxAvpTableReader.read(in, VENDOR_ID, List.of(CAPTION));
        }

        // THEN the row is not silently dropped, the leading digits are taken as the code
        assertThat(defs)
                .singleElement()
                .satisfies(def -> {
                    assertThat(def.attributeName()).isEqualTo("Digest-Realm");
                    assertThat(def.avpCode()).isEqualTo(104L);
                    assertThat(def.vendorId()).isEqualTo(VENDOR_ID);
                    assertThat(def.valueType()).isEqualTo("UTF8String");
                });
    }

    @Test
    void it_emits_vendor_id_zero_when_the_V_bit_must_not_be_set() throws IOException {
        // GIVEN a docx with a captioned AVP table whose row carries "V" in the "Must not" column
        final byte[] docx = buildDocxWithIetfDigestRealmRow();

        // WHEN the reader parses it with a 3GPP vendor id
        final List<AvpDef> defs;
        try (final InputStream in = new ByteArrayInputStream(docx)) {
            defs = DocxAvpTableReader.read(in, VENDOR_ID, List.of(CAPTION));
        }

        // THEN the AVP is emitted with vendor id 0, because V "must not" means it is not vendor-specific
        assertThat(defs)
                .singleElement()
                .satisfies(def -> {
                    assertThat(def.attributeName()).isEqualTo("Digest-Realm");
                    assertThat(def.vendorSpecificBit()).isEqualTo(AvpFlagRule.MUST_NOT);
                    assertThat(def.vendorId()).isZero();
                });
    }

    private static byte[] buildDocxWithIetfDigestRealmRow() throws IOException {
        try (final XWPFDocument doc = new XWPFDocument();
             final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final XWPFParagraph captionParagraph = doc.createParagraph();
            captionParagraph.createRun().setText(CAPTION);

            final XWPFTable table = doc.createTable(2, 7);
            writeHeaderRow(table.getRow(0));
            writeIetfDigestRealmRow(table.getRow(1));

            doc.write(out);
            return out.toByteArray();
        }
    }

    private static void writeIetfDigestRealmRow(final XWPFTableRow row) {
        setCellText(row.getCell(0), "Digest-Realm");
        setCellText(row.getCell(1), "104");
        setCellText(row.getCell(2), "UTF8String");
        setCellText(row.getCell(3), "M");
        setCellText(row.getCell(4), "");
        setCellText(row.getCell(5), "");
        setCellText(row.getCell(6), "V");
    }

    private static byte[] buildDocxWithDigestRealmRow() throws IOException {
        try (final XWPFDocument doc = new XWPFDocument();
             final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final XWPFParagraph captionParagraph = doc.createParagraph();
            captionParagraph.createRun().setText(CAPTION);

            final XWPFTable table = doc.createTable(2, 7);
            writeHeaderRow(table.getRow(0));
            writeDigestRealmRow(table.getRow(1));

            doc.write(out);
            return out.toByteArray();
        }
    }

    private static void writeHeaderRow(final XWPFTableRow row) {
        setCellText(row.getCell(0), "Attribute Name");
        setCellText(row.getCell(1), "AVP Code");
        setCellText(row.getCell(2), "Value Type");
        setCellText(row.getCell(3), "Must");
        setCellText(row.getCell(4), "May");
        setCellText(row.getCell(5), "Should not");
        setCellText(row.getCell(6), "Must not");
    }

    private static void writeDigestRealmRow(final XWPFTableRow row) {
        setCellText(row.getCell(0), "Digest-Realm");
        setCellLines(row.getCell(1), "104", "NOTE 3");
        setCellText(row.getCell(2), "UTF8String");
        setCellText(row.getCell(3), "M");
        setCellText(row.getCell(4), "");
        setCellText(row.getCell(5), "V");
        setCellText(row.getCell(6), "");
    }

    private static void setCellText(final XWPFTableCell cell, final String text) {
        cell.removeParagraph(0);
        cell.addParagraph().createRun().setText(text);
    }

    private static void setCellLines(final XWPFTableCell cell, final String... lines) {
        cell.removeParagraph(0);
        for (final String line : lines) {
            cell.addParagraph().createRun().setText(line);
        }
    }
}
