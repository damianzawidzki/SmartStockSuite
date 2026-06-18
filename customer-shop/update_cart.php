<?php
session_start();

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: cart.php");
    exit;
}

$quantities = $_POST["quantities"] ?? array();

foreach ($quantities as $productId => $quantity) {
    $productId = intval($productId);
    $quantity = intval($quantity);

    if ($quantity <= 0) {
        unset($_SESSION["cart"][$productId]);
    } else {
        $_SESSION["cart"][$productId] = $quantity;
    }
}

header("Location: cart.php?updated=1");
exit;