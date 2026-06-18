<?php
session_start();

$productId = intval($_GET["product_id"] ?? 0);

if ($productId > 0 && isset($_SESSION["cart"][$productId])) {
    unset($_SESSION["cart"][$productId]);
}

header("Location: cart.php?removed=1");
exit;