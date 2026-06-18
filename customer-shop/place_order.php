<?php
session_start();
require_once "products_data.php";

// Sends customer order data to the SmartStockSuite API.
$apiUrl = "https://localhost:7143/api/CustomerOrders";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: index.php");
    exit;
}

$cart = $_SESSION["cart"] ?? array();

if (count($cart) === 0) {
    header("Location: cart.php");
    exit;
}

$customerName = trim($_POST["customer_name"] ?? "");
$customerEmail = trim($_POST["customer_email"] ?? "");
$shippingAddress = trim($_POST["shipping_address"] ?? "");

if ($customerName === "" || $customerEmail === "" || $shippingAddress === "") {
    header("Location: checkout.php?error=" . urlencode("Please complete all customer details."));
    exit;
}

$orderItems = array();

foreach ($cart as $productId => $quantity) {
    $product = getProductById($productId);

    if ($product !== null && (int)$quantity > 0) {
        $orderItems[] = array(
            "productId" => (int)$productId,
            "quantity" => (int)$quantity
        );
    }
}

if (count($orderItems) === 0) {
    header("Location: cart.php");
    exit;
}

$orderData = array(
    "customerName" => $customerName,
    "customerEmail" => $customerEmail,
    "shippingAddress" => $shippingAddress,
    "items" => $orderItems
);

$ch = curl_init($apiUrl);

curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, array(
    "Content-Type: application/json"
));
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($orderData));

// Development only: allow local HTTPS certificate from ASP.NET Core.
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, false);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$error = curl_error($ch);

curl_close($ch);

if ($response === false || $error) {
    header("Location: checkout.php?error=" . urlencode("Cannot connect to SmartStockSuite API. Make sure Visual Studio backend is running on https://localhost:7143. Error: " . $error));
    exit;
}

$responseData = json_decode($response, true);

if ($httpCode < 200 || $httpCode >= 300) {
    $errorMessage = $response !== "" ? $response : "Order could not be created.";
    header("Location: checkout.php?error=" . urlencode("API error " . $httpCode . ": " . $errorMessage));
    exit;
}

$orderId = $responseData["customerOrderId"] ?? 0;
$totalAmount = $responseData["totalAmount"] ?? 0;

$_SESSION["cart"] = array();

header("Location: order_success.php?order_id=" . urlencode($orderId) . "&total=" . urlencode($totalAmount));
exit;