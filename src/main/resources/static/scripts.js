document.getElementById('addItem').addEventListener('click', addItemToCart);
document.getElementById('processPurchase').addEventListener('click', processPurchase);
document.getElementById('downloadPdf').addEventListener('click', downloadReceiptAsPDF);
document.getElementById('backToDashboard').addEventListener('click', function() {
    window.location.href = 'index.html'; // Replace 'dashboard.html' with the path to your dashboard page
});

let cart = [];
let lastTransaction = null;

function addItemToCart() {
    const itemCode = document.getElementById('itemCode').value;
    const quantity = parseInt(document.getElementById('quantity').value); // Ensure quantity is an integer

    fetch(`/api/items/${itemCode}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Item not found or server error');
            }
            return response.json();
        })
        .then(item => {
            item.quantity = quantity; // Add quantity to item object
            cart.push({ code: item.code, name: item.name, price: item.price, quantity: item.quantity }); // Ensure correct structure
            displayCart();
        })
        .catch(error => console.error('Error:', error));
}

function displayCart() {
    const cartItems = document.getElementById('cartItems');
    cartItems.innerHTML = '';
    cart.forEach(item => {
        const li = document.createElement('li');
        li.textContent = `Code: ${item.code}, Name: ${item.name}, Quantity: ${item.quantity}, Price: ${item.price}`;
        cartItems.appendChild(li);
    });
}

function processPurchase() {
    const cashTendered = parseFloat(document.getElementById('cashTendered').value); // Ensure cashTendered is a float
    const discount = parseFloat(document.getElementById('discount').value); // Ensure discount is a float

    const purchase = {
        items: cart,
        cashTendered: cashTendered,
        discount: discount,
        date: new Date().toISOString().split('T')[0] // Send current date in YYYY-MM-DD format
    };

    console.log('Sending purchase request:', purchase); // Log the data being sent

    fetch('/api/purchases', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(purchase)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Purchase failed or server error');
        }
        return response.json();
    })
    .then(data => {
        displayReceipt(data);
        lastTransaction = data; // Store the last transaction for PDF generation
        console.log('Last Transaction:', lastTransaction); // Log the last transaction
        cart = [];
        displayCart();
    })
    .catch(error => console.error('Error:', error));
}

function displayReceipt(transaction) {
    const receiptDetails = document.getElementById('receiptDetails');
    receiptDetails.innerHTML = `Transaction ID: ${transaction.id}<br>
        Date: ${new Date(transaction.date).toLocaleString()}<br>
        Total Price: LKR ${transaction.totalPrice}<br>
        Discount: LKR ${transaction.discount}<br>
        Cash Tendered: LKR ${transaction.cashTendered}<br>
        Change Amount: LKR ${transaction.changeAmount}<br>`;
}

function downloadReceiptAsPDF() {
    if (!lastTransaction) {
        alert('No transaction available to download.');
        return;
    }

    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    // Set font styles
    doc.setFont("times", "normal");
    doc.setFontSize(12);

    // Add store name
    const storeName = "Synex Store";
    doc.setFont("times", "bold");
    doc.setFontSize(20);
    doc.text(storeName, 105, 20, null, null, 'center');

    // Add transaction details
    const date = new Date(lastTransaction.date).toLocaleString();
    const transactionID = lastTransaction.id;
    const totalPrice = lastTransaction.totalPrice;
    const discount = lastTransaction.discount;
    const cashTendered = lastTransaction.cashTendered;
    const changeAmount = lastTransaction.changeAmount;

    doc.setFont("times", "normal");
    doc.setFontSize(12);
    doc.text(`Transaction ID: ${transactionID}`, 20, 40);
    doc.text(`Date: ${date}`, 20, 50);

    // Add item details
    let startY = 60;
    doc.setFont("times", "italic");
    lastTransaction.items.forEach(item => {
        const itemName = item.item.name;
        const itemPrice = item.item.price;
        const itemQuantity = item.quantity;
        doc.text(`${itemName}: $${itemPrice} x ${itemQuantity} = $${(itemPrice * itemQuantity).toFixed(2)}`, 20, startY);
        startY += 10;
    });

    // Add total price, discount, cash tendered, and change amount
    startY += 10;
    doc.text(`Total Price: LKR ${totalPrice.toFixed(2)}`, 20, startY);
    startY += 10;
    doc.text(`Discount: LKR ${discount.toFixed(2)}`, 20, startY);
    startY += 10;
    doc.text(`Cash Tendered: LKR ${cashTendered.toFixed(2)}`, 20, startY);
    startY += 10;
    doc.text(`Change Amount: LKR ${changeAmount.toFixed(2)}`, 20, startY);

    // Add goodbye message
    doc.setFont("times", "normal");
    doc.setFontSize(12);
    startY += 20;
    doc.setLineWidth(0.5);
    doc.line(20, startY, 190, startY); // Horizontal line
    startY += 10;
    doc.text("Good bye. Come again!", 105, startY, null, null, 'center');

    // Save the PDF
    doc.save('receipt.pdf');
}

