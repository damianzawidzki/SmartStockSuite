<?php
$orderId = $_GET["order_id"] ?? "N/A";
$totalAmount = $_GET["total"] ?? "0";
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Thank You | PrimeStock Warehouse</title>
    <link rel="stylesheet" href="style.css?v=11">
</head>
<body>

<main class="success-page">
    <div class="success-card">
        <img src="assets/logo.png" alt="PrimeStock Warehouse Logo" class="success-logo">

        <div class="success-icon">✓</div>

        <h1>Thank You for Your Order</h1>

        <p>
            Your order has been successfully sent to PrimeStock Warehouse.
            The warehouse team will now prepare it for picking and barcode verification.
        </p>

        <div class="order-summary-box">
            <div>
                <span>Order ID</span>
                <strong><?php echo htmlspecialchars($orderId); ?></strong>
            </div>

            <div>
                <span>Total Amount</span>
                <strong>£<?php echo number_format((float)$totalAmount, 2); ?></strong>
            </div>
        </div>

        <p class="small-note">
            You can now return to the product page and create another order.
        </p>

        <a href="index.php" class="back-button">Back to Products</a>
    </div>
</main>

</body>
</html>