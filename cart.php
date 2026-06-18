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
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Cart | PrimeStock Warehouse</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="style.css?v=10">
</head>
<body>

<header class="simple-header">
    <div class="brand">
        <img src="assets/logo.png" alt="PrimeStock Warehouse Logo">
        <div>
            <h1>PrimeStock Warehouse</h1>
            <p>Shopping Cart</p>
        </div>
    </div>

    <nav class="nav-menu">
        <a href="index.php">Continue Shopping</a>
        <a href="cart.php">Cart</a>
    </nav>
</header>

<main>
    <section class="section-title left-title">
        <span>Basket</span>
        <h2>Your Cart</h2>
        <p>Review products before checkout.</p>
    </section>

    <?php if (isset($_GET["updated"])): ?>
        <div class="alert success">Cart updated successfully.</div>
    <?php endif; ?>

    <?php if (isset($_GET["removed"])): ?>
        <div class="alert success">Product removed from cart.</div>
    <?php endif; ?>

    <?php if (count($cartItems) === 0): ?>
        <div class="empty-card">
            <h3>Your cart is empty.</h3>
            <p>Add products from the shop to create a customer order.</p>
            <a class="primary-button" href="index.php">Back to Products</a>
        </div>
    <?php else: ?>

        <form action="update_cart.php" method="POST">
            <div class="cart-panel">
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Product ID</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                            <th>Line Total</th>
                            <th>Remove</th>
                        </tr>
                    </thead>

                    <tbody>
                        <?php foreach ($cartItems as $item): ?>
                            <?php $product = $item["product"]; ?>
                            <tr>
                                <td>
                                    <div class="cart-product">
                                        <img src="<?php echo htmlspecialchars($product["image"]); ?>" alt="<?php echo htmlspecialchars($product["name"]); ?>">
                                        <div>
                                            <strong><?php echo htmlspecialchars($product["name"]); ?></strong>
                                            <span><?php echo htmlspecialchars($product["category"]); ?></span>
                                        </div>
                                    </div>
                                </td>

                                <td><?php echo htmlspecialchars((string)$product["productId"]); ?></td>

                                <td>£<?php echo number_format($product["price"], 2); ?></td>

                                <td>
                                    <input
                                        class="quantity-input"
                                        type="number"
                                        name="quantities[<?php echo htmlspecialchars((string)$product["productId"]); ?>]"
                                        value="<?php echo htmlspecialchars((string)$item["quantity"]); ?>"
                                        min="1"
                                    >
                                </td>

                                <td>£<?php echo number_format($item["lineTotal"], 2); ?></td>

                                <td>
                                    <a class="danger-link" href="remove_from_cart.php?product_id=<?php echo htmlspecialchars((string)$product["productId"]); ?>">
                                        Remove
                                    </a>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>

                <div class="cart-summary">
                    <div>
                        <span>Total Amount</span>
                        <strong>£<?php echo number_format($totalAmount, 2); ?></strong>
                    </div>

                    <div class="cart-actions">
                        <button type="submit" class="secondary-submit">Update Cart</button>
                        <a href="checkout.php" class="primary-button">Proceed to Checkout</a>
                    </div>
                </div>
            </div>
        </form>

    <?php endif; ?>
</main>

</body>
</html>