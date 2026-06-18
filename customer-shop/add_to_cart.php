<?php
session_start();
require_once "products_data.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: index.php");
    exit;
}

$productId = intval($_POST["product_id"] ?? 0);
$quantity = intval($_POST["quantity"] ?? 0);

$product = getProductById($productId);

if ($product === null || $quantity <= 0) {
    header("Location: index.php?error=" . urlencode("Invalid product or quantity."));
    exit;
}

if (!isset($_SESSION["cart"])) {
    $_SESSION["cart"] = array();
}

if (isset($_SESSION["cart"][$productId])) {
    $_SESSION["cart"][$productId] += $quantity;
} else {
    $_SESSION["cart"][$productId] = $quantity;
}

header("Location: index.php?success=1");
exit;