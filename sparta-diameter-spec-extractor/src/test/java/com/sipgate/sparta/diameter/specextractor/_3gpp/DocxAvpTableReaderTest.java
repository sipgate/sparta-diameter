package com.sipgate.sparta.diameter.specextractor._3gpp;

import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpFlagRule;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    void it_matches_a_header_cell_whose_label_carries_a_trailing_parenthetical_annotation() throws IOException {
        // GIVEN a docx whose "Value Type" header cell is labelled "Value Type (NOTE 2)" (as in TS 29.212)
        final byte[] docx = buildDocxWithAnnotatedValueTypeHeader();

        // WHEN the reader parses it
        final List<AvpDef> defs;
        try (final InputStream in = new ByteArrayInputStream(docx)) {
            defs = DocxAvpTableReader.read(in, VENDOR_ID, List.of(CAPTION));
        }

        // THEN the table is still recognised and the value type is read out of the annotated column
        assertThat(defs)
                .singleElement()
                .satisfies(def -> {
                    assertThat(def.attributeName()).isEqualTo("Digest-Realm");
                    assertThat(def.avpCode()).isEqualTo(104L);
                    assertThat(def.valueType()).isEqualTo("UTF8String");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Sponsored-Connectivity-Data (NOTE 4)",
        "Sponsored-Connectivity-Data (NOTE 4)",
    })
    void it_strips_a_trailing_NOTE_annotation_from_the_attribute_name(final String name) throws IOException {
        // GIVEN a docx whose name cell reads "Sponsored-Connectivity-Data (NOTE 4)" with a non-breaking space (as in TS 29.214)
        final byte[] docx = buildDocxWithName(name);

        // WHEN the reader parses it
        final List<AvpDef> defs;
        try (final InputStream in = new ByteArrayInputStream(docx)) {
            defs = DocxAvpTableReader.read(in, VENDOR_ID, List.of(CAPTION));
        }

        // THEN the footnote marker and non-breaking space are dropped from the name
        assertThat(defs)
                .singleElement()
                .satisfies(def -> assertThat(def.attributeName()).isEqualTo("Sponsored-Connectivity-Data"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Enumerate", "Enumarated"})
    void it_rewrites_spec_typos_of_Enumerated(final String typo) throws IOException {
        // GIVEN a docx whose row has a known typo for "Enumerated" as its value type
        final byte[] docx = buildDocxWithValueType(typo);

        // WHEN the reader parses it
        final List<AvpDef> defs;
        try (final InputStream in = new ByteArrayInputStream(docx)) {
            defs = DocxAvpTableReader.read(in, VENDOR_ID, List.of(CAPTION));
        }

        // THEN the typo is corrected to the canonical "Enumerated"
        assertThat(defs)
                .singleElement()
                .satisfies(def -> assertThat(def.valueType()).isEqualTo("Enumerated"));
    }

    private static byte[] buildDocxWithName(final String name) throws IOException {
        try (final XWPFDocument doc = new XWPFDocument();
             final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final XWPFParagraph captionParagraph = doc.createParagraph();
            captionParagraph.createRun().setText(CAPTION);

            final XWPFTable table = doc.createTable(2, 7);
            writeHeaderRow(table.getRow(0));
            final XWPFTableRow row = table.getRow(1);
            setCellText(row.getCell(0), name);
            setCellText(row.getCell(1), "530");
            setCellText(row.getCell(2), "Grouped");
            setCellText(row.getCell(3), "V");
            setCellText(row.getCell(4), "");
            setCellText(row.getCell(5), "");
            setCellText(row.getCell(6), "M");

            doc.write(out);
            return out.toByteArray();
        }
    }

    private static byte[] buildDocxWithValueType(final String valueType) throws IOException {
        try (final XWPFDocument doc = new XWPFDocument();
             final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final XWPFParagraph captionParagraph = doc.createParagraph();
            captionParagraph.createRun().setText(CAPTION);

            final XWPFTable table = doc.createTable(2, 7);
            writeHeaderRow(table.getRow(0));
            writeAlertReasonRow(table.getRow(1), valueType);

            doc.write(out);
            return out.toByteArray();
        }
    }

    private static void writeAlertReasonRow(final XWPFTableRow row, final String valueType) {
        setCellText(row.getCell(0), "Alert-Reason");
        setCellText(row.getCell(1), "1434");
        setCellText(row.getCell(2), valueType);
        setCellText(row.getCell(3), "M,V");
        setCellText(row.getCell(4), "");
        setCellText(row.getCell(5), "");
        setCellText(row.getCell(6), "");
    }

    private static byte[] buildDocxWithAnnotatedValueTypeHeader() throws IOException {
        try (final XWPFDocument doc = new XWPFDocument();
             final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final XWPFParagraph captionParagraph = doc.createParagraph();
            captionParagraph.createRun().setText(CAPTION);

            final XWPFTable table = doc.createTable(2, 7);
            writeHeaderRowWithAnnotatedValueType(table.getRow(0));
            writeIetfDigestRealmRow(table.getRow(1));

            doc.write(out);
            return out.toByteArray();
        }
    }

    private static void writeHeaderRowWithAnnotatedValueType(final XWPFTableRow row) {
        setCellText(row.getCell(0), "Attribute Name");
        setCellText(row.getCell(1), "AVP Code");
        setCellText(row.getCell(2), "Value Type (NOTE 2)");
        setCellText(row.getCell(3), "Must");
        setCellText(row.getCell(4), "May");
        setCellText(row.getCell(5), "Should not");
        setCellText(row.getCell(6), "Must not");
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
