<?php
// Loads products from SmartStockSuite API into the PHP customer shop.
// This file must be in the same folder as index.php inside XAMPP htdocs.

function getProducts()
{
    $apiUrl = "https://localhost:7143/api/Products/public";

    $ch = curl_init($apiUrl);

    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    // Development only: allow local ASP.NET Core HTTPS certificate.
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, false);

    $response = curl_exec($ch);
    $error = curl_error($ch);

    curl_close($ch);

    if ($response === false || $error) {
        return array();
    }

    $products = json_decode($response, true);

    if (!is_array($products)) {
        return array();
    }

    $mappedProducts = array();

    foreach ($products as $product) {
        $productName = $product["productName"] ?? "Unknown Product";
        $image = $product["imageUrl"] ?? "";

        if ($image === "" || $image === null) {
            $image = "https://via.placeholder.com/600x400?text=" . urlencode($productName);
        } else {
            if (str_starts_with($image, "/products/")) {
                $image = ltrim($image, "/");
            }
        }

        $mappedProducts[] = array(
            "productId" => $product["productId"] ?? 0,
            "name" => $productName,
            "category" => $product["category"] ?? "Uncategorised",
            "price" => $product["unitPrice"] ?? 0,
            "quantity" => $product["quantity"] ?? 0,
            "barcode" => $product["barcode"] ?? "",
            "description" => "Warehouse product connected to SmartStockSuite inventory database.",
            "image" => $image
        );
    }

    return $mappedProducts;
}

function getProductById($productId)
{
    $products = getProducts();

    foreach ($products as $product) {
        if ((int)$product["productId"] === (int)$productId) {
            return $product;
        }
    }

    return null;
}