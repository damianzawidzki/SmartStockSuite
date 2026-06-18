<?php
session_start();
require_once "products_data.php";

$cart = $_SESSION["cart"] ?? array();
$cartItems = array();
$totalAmount = 0;

foreach ($cart as $productId => $quantity) {
    $product = getProductById($productId);

    if ($product !== null) {
        $lineTotal = $product["price"] * (int)$quantity;
        $totalAmount += $lineTotal;

        $cartItems[] = array(
            "product" => $product,
            "quantity" => (int)$quantity,
            "lineTotal" => $lineTotal
        );
    }
}

if (count($cartItems) === 0) {
    header("Location: cart.php");
    exit;
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Checkout | PrimeStock Warehouse</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="style.css?v=10">
</head>
<body>

<header class="simple-header">
    <div class="brand">
        <img src="assets/logo.png" alt="PrimeStock Warehouse Logo">
        <div>
            <h1>PrimeStock Warehouse</h1>
            <p>Checkout</p>
        </div>
    </div>

    <nav class="nav-menu">
        <a href="index.php">Products</a>
        <a href="cart.php">Cart</a>
    </nav>
</header>

<main>
    <section class="checkout-layout">
        <div class="checkout-form-card">
            <div class="section-title left-title">
                <span>Checkout</span>
                <h2>Customer Details</h2>
                <p>Enter customer information to create a warehouse order.</p>
            </div>

            <?php if (isset($_GET["error"])): ?>
                <div class="alert error">
                    <?php echo htmlspecialchars($_GET["error"]); ?>
                </div>
            <?php endif; ?>

            <form action="place_order.php" method="POST" class="checkout-form">
                <label>
                    Customer Name
                    <input type="text" name="customer_name" placeholder="Test Customer" required>
                </label>

                <label>
                    Customer Email
                    <input type="email" name="customer_email" placeholder="test@email.com" required>
                </label>

                <label>
                    Shipping Address
                    <textarea name="shipping_address" placeholder="12 Test Street, Leicester" required></textarea>
                </label>

                <button type="submit">Place Order</button>
            </form>
        </div>

        <aside class="checkout-summary">
            <h3>Order Summary</h3>

            <?php foreach ($cartItems as $item): ?>
                <?php $product = $item["product"]; ?>
                <div class="summary-line">
                    <div>
                        <strong><?php echo htmlspecialchars($product["name"]); ?></strong>
                        <span>Qty: <?php echo htmlspecialchars((string)$item["quantity"]); ?></span>
                    </div>

                    <p>£<?php echo number_format($item["lineTotal"], 2); ?></p>
                </div>
            <?php endforeach; ?>

            <div class="summary-total">
                <span>Total</span>
                <strong>£<?php echo number_format($totalAmount, 2); ?></strong>
            </div>
        </aside>
    </section>
</main>

</body>
</html>