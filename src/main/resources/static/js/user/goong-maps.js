// Goong Maps Integration for Address Selection
// File: /js/user/goong-maps.js

// Goong Maps Configuration
// Map Tiles Key (for displaying map)
goongjs.accessToken = '4pXmjW7ligFfpjNVlkIx7pvbzKHU3KCkhFRZKRR2';

// API Key (for geocoding, places)
const GOONG_API_KEY = 'xaYvtvHWHGQswPol8J4GZX1LFRcC5pCsJmCfOcOU';

let map, marker;

function initGoongMap() {
    console.log('Initializing Goong Map with API key:', goongjs.accessToken);
    
    try {
        // Khởi tạo bản đồ (Hồ Chí Minh)
        map = new goongjs.Map({
            container: 'goongMap',
            style: 'https://tiles.goong.io/assets/goong_map_web.json',
            center: [106.6297, 10.8231], // [lng, lat]
            zoom: 13,
            attributionControl: false
        });

        map.on('load', function() {
            console.log('Goong Map loaded successfully!');
        });

        map.on('error', function(e) {
            console.error('Goong Map error:', e);
        });

        // Thêm marker
        marker = new goongjs.Marker({
            draggable: true,
            color: '#d6334d' // Màu đỏ hồng như theme
        })
        .setLngLat([106.6297, 10.8231])
        .addTo(map);

        // Click vào bản đồ
        map.on('click', function(e) {
            marker.setLngLat(e.lngLat);
            updateAddressFromCoords(e.lngLat.lng, e.lngLat.lat);
        });

        // Kéo marker
        marker.on('dragend', function() {
            const lngLat = marker.getLngLat();
            updateAddressFromCoords(lngLat.lng, lngLat.lat);
        });

        // Tìm kiếm địa điểm
        document.getElementById('mapSearchInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                searchPlace();
            }
        });

    } catch (error) {
        console.error('Error initializing Goong Map:', error);
    }
}

// Tìm kiếm địa điểm
function searchPlace() {
    const query = document.getElementById('mapSearchInput').value;
    if (!query) return;
    
    console.log('Searching for:', query);
    
    fetch(`https://rsapi.goong.io/Place/AutoComplete?api_key=${GOONG_API_KEY}&input=${encodeURIComponent(query)}`)
        .then(response => {
            console.log('Search response status:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('Search data:', data);
            if (data.predictions && data.predictions.length > 0) {
                const placeId = data.predictions[0].place_id;
                getPlaceDetail(placeId);
            } else {
                alert('Không tìm thấy địa điểm này!');
            }
        })
        .catch(error => {
            console.error('Search error:', error);
            alert('Lỗi tìm kiếm địa điểm! Kiểm tra API key và domain restrictions.');
        });
}

// Lấy chi tiết địa điểm
function getPlaceDetail(placeId) {
    fetch(`https://rsapi.goong.io/Place/Detail?place_id=${placeId}&api_key=${GOONG_API_KEY}`)
        .then(response => {
            console.log('Place detail response status:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('Place detail data:', data);
            if (data.result && data.result.geometry) {
                const location = data.result.geometry.location;
                map.flyTo({
                    center: [location.lng, location.lat],
                    zoom: 16
                });
                marker.setLngLat([location.lng, location.lat]);
                
                // Cập nhật form
                document.getElementById('selectedAddress').value = data.result.formatted_address || data.result.name;
                document.getElementById('latitude').value = location.lat;
                document.getElementById('longitude').value = location.lng;
            }
        })
        .catch(error => {
            console.error('Place detail error:', error);
        });
}

// Chuyển tọa độ thành địa chỉ
function updateAddressFromCoords(lng, lat) {
    console.log('Reverse geocoding:', lat, lng);
    
    fetch(`https://rsapi.goong.io/Geocode?latlng=${lat},${lng}&api_key=${GOONG_API_KEY}`)
        .then(response => {
            console.log('Geocoding response status:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('Geocoding data:', data);
            if (data.results && data.results.length > 0) {
                const result = data.results[0];
                document.getElementById('selectedAddress').value = result.formatted_address;
                document.getElementById('latitude').value = lat;
                document.getElementById('longitude').value = lng;
                
                // Parse address components
                parseVietnameseAddress(result);
            }
        })
        .catch(error => {
            console.error('Geocoding error:', error);
        });
}

// Parse địa chỉ Việt Nam - PHIÊN BẢN ĐƠN GIẢN
function parseVietnameseAddress(result) {
    const fullAddress = result.formatted_address || '';
    console.log('📍 Full address:', fullAddress);
    
    // Tách địa chỉ theo dấu phẩy
    // VD: "Toyota Dũng Tiến, 233 Đại Lộ Hùng Vương, Phường 5, Tuy Hòa, Phú Yên"
    const parts = fullAddress.split(',').map(p => p.trim());
    
    let street = '';
    let city = '';
    
    if (parts.length >= 2) {
        // Lấy phần cuối là Tỉnh/Thành phố
        city = parts[parts.length - 1];
        
        // Tất cả phần còn lại (từ đầu đến phần cuối - 1) là "Số nhà, tên đường"
        street = parts.slice(0, -1).join(', ');
    } else {
        // Nếu không parse được, lấy toàn bộ
        street = fullAddress;
    }
    
    console.log('✅ Parsed simple:', { street, city });
    
    // ✅ Fill Số nhà, tên đường (bao gồm phường, quận)
    const streetInput = document.getElementById('street');
    if (streetInput) {
        streetInput.value = street;
        console.log('Street filled:', street);
    }
    
    // ✅ Fill Tỉnh/Thành phố
    const cityInput = document.getElementById('city');
    if (cityInput) {
        cityInput.value = city;
        console.log('City filled:', city);
    }
}

// Khởi tạo map khi modal mở
$(document).ready(function() {
    $('#addAddressModal').on('shown.bs.modal', function() {
        if (!map) {
            setTimeout(initGoongMap, 200); // Delay để đảm bảo modal đã render
        } else {
            map.resize(); // Resize map nếu đã tồn tại
        }
    });

    // Reset form khi modal đóng
    $('#addAddressModal').on('hidden.bs.modal', function () {
        // Reset các field nếu cần
        if (document.getElementById('selectedAddress')) {
            document.getElementById('selectedAddress').value = '';
        }
        if (document.getElementById('mapSearchInput')) {
            document.getElementById('mapSearchInput').value = '';
        }
    });
});
