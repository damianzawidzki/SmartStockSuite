<?php
session_start();
require_once "products_data.php";

$products = getProducts();
$cartCount = 0;

if (isset($_SESSION["cart"])) {
    foreach ($_SESSION["cart"] as $quantity) {
        $cartCount += (int)$quantity;
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>PrimeStock Warehouse | Customer Shop</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="style.css?v=20">
</head>
<body>

<header class="site-header">
    <div class="topbar">
        <div class="brand">
            <img src="assets/logo.png" alt="PrimeStock Warehouse Logo">
            <div>
                <h1>PrimeStock Warehouse</h1>
                <p>Customer Shopping Portal</p>
            </div>
        </div>

        <nav class="nav-menu">
            <a href="index.php">Products</a>
            <a href="cart.php">Cart (<?php echo $cartCount; ?>)</a>
            <a href="#process">Process</a>
        </nav>
    </div>

    <section class="hero">
        <div class="hero-text">
            <span class="badge">SmartStockSuite Customer Demo</span>
            <h2>Order products directly from the warehouse inventory system.</h2>
            <p>
                This shop loads products from the SmartStockSuite database.
                Customers can add products to the basket and create customer orders
                for warehouse picking and barcode verification.
            </p>

            <div class="hero-actions">
                <a href="#products" class="primary-button">Shop Products</a>
                <a href="cart.php" class="secondary-button">View Cart</a>
            </div>
        </div>

        <div class="hero-card">
            <h3>Warehouse Order Flow</h3>

            <div class="flow-step">
                <span>1</span>
                <p>Customer adds products to cart</p>
            </div>

            <div class="flow-step">
                <span>2</span>
                <p>Order is saved in SmartStockSuite</p>
            </div>

            <div class="flow-step">
                <span>3</span>
                <p>Manager releases order for picking</p>
            </div>

            <div class="flow-step">
                <span>4</span>
                <p>Picker scans product barcode</p>
            </div>
        </div>
    </section>
</header>

<main>
    <?php if (isset($_GET["success"])): ?>
        <div class="alert success">
            Product added to cart successfully.
        </div>
    <?php endif; ?>

    <?php if (isset($_GET["error"])): ?>
        <div class="alert error">
            <?php echo htmlspecialchars($_GET["error"]); ?>
        </div>
    <?php endif; ?>

    <section id="process" class="info-section">
        <div class="section-title">
            <span>Connected Workflow</span>
            <h2>Customer shop connected to warehouse fulfilment</h2>
            <p>
                The customer shop uses live product data from the SmartStockSuite API.
                Orders are submitted to the warehouse system and can then be released
                for picking.
            </p>
        </div>

        <div class="info-grid">
            <div class="info-card">
                <strong>01</strong>
                <h3>Add to Cart</h3>
                <p>The customer adds one or more live database products to the basket.</p>
            </div>

            <div class="info-card">
                <strong>02</strong>
                <h3>Checkout</h3>
                <p>The customer enters name, email and delivery address.</p>
            </div>

            <div class="info-card">
                <strong>03</strong>
                <h3>Warehouse Order</h3>
                <p>The order is saved in the SmartStockSuite database.</p>
            </div>

            <div class="info-card">
                <strong>04</strong>
                <h3>Picking</h3>
                <p>The picker fulfils the order using barcode verification.</p>
            </div>
        </div>
    </section>

    <section id="products" class="products-section">
        <div class="section-title">
            <span>Products</span>
            <h2>Available Products</h2>
            <p>
                Products below are loaded from the SmartStockSuite database through the API.
            </p>
        </div>

        <?php if (count($products) === 0): ?>
            <div class="empty-card">
                <h3>No products loaded.</h3>
                <p>
                    Make sure the ASP.NET Core backend is running and the endpoint
                    <strong>https://localhost:7143/api/Products/public</strong> works.
                </p>
            </div>
        <?php else: ?>

            <div class="product-grid">
                <?php foreach ($products as $product): ?>
                    <article class="product-card">
                        <div class="product-image-wrapper">
                            <img
                                src="<?php echo htmlspecialchars($product["image"]); ?>"
                                alt="<?php echo htmlspecialchars($product["name"]); ?>"
                            >
                        </div>

                        <div class="product-content">
                            <div class="product-top">
                                <span class="category">
                                    <?php echo htmlspecialchars($product["category"]); ?>
                                </span>

                                <span class="product-id">
                                    ID: <?php echo htmlspecialchars((string)$product["productId"]); ?>
                                </span>
                            </div>

                            <h3><?php echo htmlspecialchars($product["name"]); ?></h3>

                            <p>
                                <?php echo htmlspecialchars($product["description"]); ?>
                            </p>

                            <p style="margin-top: 10px; font-size: 13px; color: #667085;">
                                Barcode:
                                <?php echo htmlspecialchars($product["barcode"] ?: "N/A"); ?>
                            </p>

                            <p style="margin-top: 4px; font-size: 13px; color: #667085;">
                                Available stock:
                                <?php echo htmlspecialchars((string)$product["quantity"]); ?>
                            </p>

                            <div class="price">
                                £<?php echo number_format((float)$product["price"], 2); ?>
                            </div>

                            <form action="add_to_cart.php" method="POST" class="cart-form">
                                <input
                                    type="hidden"
                                    name="product_id"
                                    value="<?php echo htmlspecialchars((string)$product["productId"]); ?>"
                                >

                                <label>
                                    Quantity
                                    <input
                                        type="number"
                                        name="quantity"
                                        value="1"
                                        min="1"
                                        max="<?php echo htmlspecialchars((string)$product["quantity"]); ?>"
                                        required
                                    >
                                </label>

                                <button
                                    type="submit"
                                    <?php echo ((int)$product["quantity"] <= 0) ? "disabled" : ""; ?>
                                >
                                    <?php echo ((int)$product["quantity"] <= 0) ? "Out of Stock" : "Add to Cart"; ?>
                                </button>
                            </form>
                        </div>
                    </article>
                <?php endforeach; ?>
            </div>

        <?php endif; ?>
    </section>
</main>

<footer>
    <div>
        <h3>PrimeStock Warehouse</h3>
        <p>Customer shop demo connected with SmartStockSuite warehouse management.</p>
    </div>

    <p>© 2026 PrimeStock Warehouse</p>
</footer>

</body>
</html>