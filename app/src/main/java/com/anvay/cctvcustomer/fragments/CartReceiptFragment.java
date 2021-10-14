package com.anvay.cctvcustomer.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.MainActivity;
import com.anvay.cctvcustomer.databinding.FragmentCartReceiptBinding;
import com.anvay.cctvcustomer.models.OrderItem;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.OrderViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class CartReceiptFragment extends Fragment {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private OrderItem orderItem;
    private FragmentCartReceiptBinding binding;
    private View loadingLayout;
    private TitleViewModel titleViewModel;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        String title = "Receipt";
        titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack(title);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartReceiptBinding.inflate(inflater, container, false);
        OrderViewModel orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        Button viewInvoice = binding.viewInvoice;
        loadingLayout = binding.loadingLayout.getRoot();
        boolean fromOrders = true;
        String amount = null, transactionNumber = null;
        if (getArguments() != null) {
            fromOrders = getArguments().getBoolean(Constants.KEY_FROM_ORDERS_SCREEN);
            amount = getArguments().getString(Constants.KEY_AMOUNT_TO_PAY);
            transactionNumber = getArguments().getString(Constants.KEY_TRANSACTION_NUMBER);
        }
        if (fromOrders) {
            orderItem = orderViewModel.getOrderItem();
            initUI();
        } else {
            titleViewModel.setCartPurchased(true);
            viewInvoice.setVisibility(View.INVISIBLE);
            binding.paidAmountLabel.setText(amount);
            binding.referenceNumberLabel.setText("Transaction No: " + transactionNumber);
            binding.infoLabel.setText("Your bill amount of " + amount + " is successfully paid. " +
                    "You can download invoice and track your order status from the orders section in your Dashboard.");
        }
        viewInvoice.setOnClickListener(view -> generateInvoice());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        if (orderItem == null)
            return;
        binding.viewInvoice.setVisibility(View.VISIBLE);
        double paidAmount = orderItem.getPrice() * orderItem.getQuantity();
        binding.paidAmountLabel.setText("\u20B9 " + paidAmount);
        binding.referenceNumberLabel.setText("Transaction No: " + orderItem.getTransactionId());
        binding.storeNameLabel.setText(orderItem.getStoreName());
        binding.infoLabel.setText("Your bill amount of \u20B9 " + paidAmount + " with " +
                orderItem.getStoreName() + " is successfully processed.");
    }

    private void generateInvoice() {
        try {
            createPdfWrapper();
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }
    }

    private void createPdfWrapper() throws FileNotFoundException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            storagePermission();
        } else {
            createPdf();
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(requireActivity())
                .setMessage("You need to allow access to Storage to generate Invoice")
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {
        loadingLayout.setVisibility(View.VISIBLE);
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists())
            docsFolder.mkdir();
        String pdfName = UUID.randomUUID().toString() + ".pdf";
        File pdfFile = new File(docsFolder.getAbsolutePath(), pdfName);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.NORMAL);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 28, Font.BOLD, BaseColor.MAGENTA);
        PdfPTable table = new PdfPTable(new float[]{5, 3});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("Details");
        table.addCell("Price");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (PdfPCell cell : cells) cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        Paragraph details = new Paragraph();
        PdfPCell detailsCell = new PdfPCell();
        details.setFont(normalFont);
        detailsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailsCell.setPaddingLeft(50);
        details.add(new Paragraph("Product Name: " + orderItem.getProductName()));
        details.add(new Paragraph("Product Id: " + orderItem.getProductId()));
        details.add(new Paragraph("Quantity Purchased: " + orderItem.getQuantity()));
        details.add(new Paragraph("Price per item: " + orderItem.getPrice() + "\n\n"));
        detailsCell.addElement(details);
        table.addCell(detailsCell);
        double paidAmount = orderItem.getPrice() * orderItem.getQuantity();
        table.addCell(String.valueOf(paidAmount));
        table.addCell("Total");
        table.addCell(String.valueOf(paidAmount));
        PdfWriter.getInstance(document, output);
        document.open();
        Paragraph title = new Paragraph();
        title.setFont(titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.add("INVOICE\n\n\n\n");
        document.add(title);
        Paragraph storeName = new Paragraph();
        storeName.setFont(catFont);
        storeName.setAlignment(Element.ALIGN_CENTER);
        storeName.add(orderItem.getStoreName());
        document.add(storeName);
        document.add(new Paragraph("Customer Name:    " + MainActivity.userName, normalFont));
        String paymentDate = orderItem.getDate();
        if (paymentDate == null)
            paymentDate = Constants.getDate(Constants.TIMESTAMP_DATE_TIME);
        document.add(new Paragraph("Payment Date:      " + paymentDate, normalFont));
        document.add(new Paragraph("Delivery Address: " + orderItem.getCustomerAddress(), normalFont));
        document.add(new Paragraph("Transaction No:    " + orderItem.getTransactionId() + "\n\n\n", normalFont));
        document.add(table);
        document.add(new Paragraph("\n\nNOTE: In case of any queries, please contact customer care with reference number provided."));
        document.close();
        loadingLayout.setVisibility(View.INVISIBLE);
        previewPdf(pdfFile);
    }

    private void previewPdf(File file) {
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider
                .getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "application/pdf");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void storagePermission() {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel(
                            (dialog, which) -> requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_CODE_ASK_PERMISSIONS));
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }
}
