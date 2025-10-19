/**
 * WebSocket Client cho Realtime Inventory & Sold Quantity Updates
 * Tự động cập nhật UI khi có thay đổi từ database
 */

class InventoryRealtimeClient {
    constructor() {
        this.stompClient = null;
        this.connected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
    }

    /**
     * Kết nối WebSocket server
     */
    connect() {
        console.log('\n========================================');
        console.log('🔌 CONNECTING TO WEBSOCKET SERVER');
        console.log('========================================');
        console.log('📍 Endpoint: /ws');
        
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        // Enable debug logs for troubleshooting
        this.stompClient.debug = (msg) => {
            console.log('🐛 STOMP Debug:', msg);
        };
        
        console.log('🔄 Attempting connection...');
        
        this.stompClient.connect({}, (frame) => {
            console.log('\n========================================');
            console.log('✅ WEBSOCKET CONNECTED SUCCESSFULLY!');
            console.log('========================================');
            console.log('📡 Frame:', frame);
            console.log('🌐 Connection state: CONNECTED');
            this.connected = true;
            this.reconnectAttempts = 0;
            
            // Subscribe to topics
            console.log('\n📬 Subscribing to topics...');
            this.subscribeToInventoryUpdates();
            this.subscribeToSoldQuantityUpdates();
            
            // Show connection indicator
            this.updateConnectionStatus(true);
            
            console.log('✅ All subscriptions complete!');
            console.log('========================================\n');
            
        }, (error) => {
            console.error('\n========================================');
            console.error('❌ WEBSOCKET CONNECTION FAILED!');
            console.error('========================================');
            console.error('🔥 Error:', error);
            console.error('========================================\n');
            
            this.connected = false;
            this.updateConnectionStatus(false);
            
            // Retry connection
            this.scheduleReconnect();
        });
    }

    /**
     * Subscribe to inventory updates
     */
    subscribeToInventoryUpdates() {
        console.log('🔔 Subscribing to /topic/inventory...');
        
        this.stompClient.subscribe('/topic/inventory', (message) => {
            console.log('\n========================================');
            console.log('🎉 WEBSOCKET MESSAGE RECEIVED!');
            console.log('========================================');
            console.log('📨 Raw message:', message);
            console.log('📨 Message body:', message.body);
            
            const data = JSON.parse(message.body);
            
            console.log('📦 Parsed data:', data);
            console.log('   - Product Detail ID:', data.productDetailId);
            console.log('   - Product ID:', data.productId);
            console.log('   - Product Title:', data.productTitle);
            console.log('   - Size:', data.size);
            console.log('   - New Quantity:', data.newQuantity);
            console.log('   - Update Type:', data.updateType);
            console.log('========================================\n');
            
            this.handleInventoryUpdate(data);
        });
        
        console.log('✅ Successfully subscribed to /topic/inventory');
    }

    /**
     * Subscribe to sold quantity updates
     */
    subscribeToSoldQuantityUpdates() {
        this.stompClient.subscribe('/topic/sold-quantity', (message) => {
            const data = JSON.parse(message.body);
            console.log('📊 Sold Quantity Update Received:', data);
            this.handleSoldQuantityUpdate(data);
        });
        
        console.log('✅ Subscribed to /topic/sold-quantity');
    }

    /**
     * Handle inventory update
     */
    handleInventoryUpdate(data) {
        console.log('🔄 Handling inventory update...');
        console.log('🔍 Looking for elements with data-product-detail-id:', data.productDetailId);
        
        // Update inventory cells by product detail ID
        const inventoryCells = document.querySelectorAll(`[data-product-detail-id="${data.productDetailId}"]`);
        
        console.log('📊 Found', inventoryCells.length, 'elements with data-product-detail-id');
        
        if (inventoryCells.length === 0) {
            console.warn('⚠️ NO ELEMENTS FOUND with data-product-detail-id=' + data.productDetailId);
            console.log('🔍 Searching all elements with data-product-detail-id attribute...');
            const allElements = document.querySelectorAll('[data-product-detail-id]');
            console.log('📋 All elements with data-product-detail-id:', allElements.length);
            allElements.forEach(el => {
                console.log('   - Found element with data-product-detail-id:', el.getAttribute('data-product-detail-id'));
            });
        }
        
        inventoryCells.forEach((cell, index) => {
            console.log(`📦 Processing cell ${index + 1}:`, cell);
            
            // Find quantity element inside cell
            const quantityElement = cell.querySelector('.inventory-quantity, [data-field="quantity"]');
            
            console.log('   - Quantity element:', quantityElement);
            
            if (quantityElement) {
                const oldValue = quantityElement.textContent;
                quantityElement.textContent = data.newQuantity;
                
                console.log(`   ✅ Updated: ${oldValue} → ${data.newQuantity}`);
                
                // Add flash animation
                this.flashElement(quantityElement, data.updateType === 'DECREASE' ? 'warning' : 'success');
                
                console.log(`   ✨ Applied flash animation (${data.updateType})`);
                
                // Change color based on stock level
                this.updateStockColorIndicator(quantityElement, data.newQuantity);
            } else {
                console.warn('   ⚠️ No quantity element found inside this cell');
            }
        });
        
        // Also update by product ID (for summary views)
        this.updateInventoryByProductId(data.productId);
        
        console.log('✅ Handle inventory update complete\n');
    }

    /**
     * Handle sold quantity update
     */
    handleSoldQuantityUpdate(data) {
        // Update sold quantity cells by product ID
        const soldCells = document.querySelectorAll(`[data-product-id="${data.productId}"]`);
        
        soldCells.forEach(cell => {
            // Find sold quantity element inside cell
            const soldElement = cell.querySelector('.sold-quantity, [data-field="sold-quantity"]');
            
            if (soldElement) {
                const oldValue = soldElement.textContent;
                soldElement.textContent = data.soldQuantity;
                
                // Add flash animation
                this.flashElement(soldElement, data.updateType === 'INCREASE' ? 'success' : 'info');
                
                console.log(`  Updated sold quantity: ${data.productTitle} ${oldValue} → ${data.soldQuantity}`);
            }
        });
    }

    /**
     * Update inventory by product ID (sum all sizes)
     */
    updateInventoryByProductId(productId) {
        // This is for product list view (not inventory list)
        const productRows = document.querySelectorAll(`[data-product-id="${productId}"]`);
        
        productRows.forEach(row => {
            const totalInventoryElement = row.querySelector('.total-inventory');
            if (totalInventoryElement) {
                // Optionally refetch total from server
                // For now, just add a "changed" indicator
                this.flashElement(totalInventoryElement, 'info');
            }
        });
    }

    /**
     * Flash animation for updated element
     */
    flashElement(element, type = 'success') {
        // Remove existing flash classes
        element.classList.remove('flash-success', 'flash-warning', 'flash-info');
        
        // Add new flash class
        element.classList.add(`flash-${type}`);
        
        // Remove after animation
        setTimeout(() => {
            element.classList.remove(`flash-${type}`);
        }, 2000);
    }

    /**
     * Update stock color indicator (red if low, green if good)
     */
    updateStockColorIndicator(element, quantity) {
        element.classList.remove('stock-low', 'stock-medium', 'stock-good');
        
        if (quantity === 0) {
            element.classList.add('stock-low');
            element.style.color = '#dc3545'; // Red
        } else if (quantity < 10) {
            element.classList.add('stock-medium');
            element.style.color = '#ffc107'; // Yellow
        } else {
            element.classList.add('stock-good');
            element.style.color = '#28a745'; // Green
        }
    }

    /**
     * Update connection status indicator
     */
    updateConnectionStatus(connected) {
        const statusIndicator = document.getElementById('ws-status');
        if (statusIndicator) {
            if (connected) {
                statusIndicator.className = 'badge badge-success';
                statusIndicator.innerHTML = '● Connected';
            } else {
                statusIndicator.className = 'badge badge-danger';
                statusIndicator.innerHTML = '● Disconnected';
            }
        }
    }

    /**
     * Schedule reconnection
     */
    scheduleReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000); // Exponential backoff
            
            console.log(`🔄 Reconnecting in ${delay / 1000}s (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
            
            setTimeout(() => {
                this.connect();
            }, delay);
        } else {
            console.error('❌ Max reconnection attempts reached. Please refresh the page.');
        }
    }

    /**
     * Disconnect WebSocket
     */
    disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
            console.log('🔌 WebSocket Disconnected');
        }
        this.connected = false;
        this.updateConnectionStatus(false);
    }
}

// ========== Initialize WebSocket Client ==========

let inventoryWsClient = null;

// Auto-connect when page loads
document.addEventListener('DOMContentLoaded', () => {
    console.log('📱 Initializing Inventory Realtime WebSocket Client...');
    
    inventoryWsClient = new InventoryRealtimeClient();
    inventoryWsClient.connect();
});

// Disconnect when page unloads
window.addEventListener('beforeunload', () => {
    if (inventoryWsClient) {
        inventoryWsClient.disconnect();
    }
});

// Expose to global scope for debugging
window.inventoryWsClient = inventoryWsClient;
