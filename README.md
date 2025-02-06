Run via Coursework1Application.java

## REST API for Drone Delivery

This is a REST API that uses A* to find a path between a restaurant in Edinburgh and UofE Appleton Tower.

The path is calculated around various no-fly zones, and once the drone enters a specific region near AT, it cannot leave.

### **Endpoints**

#### `calcDeliveryPath`
This takes an order, validates it, finds the restaurant, and returns a delivery path.

**Example input:**
```json
{
  "orderNo": "50DD6C5D",
  "orderDate": "2025-02-02",
  "priceTotalInPence": 2500,
  "pizzasInOrder": [
    {"name": "R1: Margarita", "priceInPence": 1000},
    {"name": "R1: Calzone", "priceInPence": 1400}
  ],
  "creditCardInformation": {
    "creditCardNumber": "4775210538642109",
    "creditCardExpiry": "05/25",
    "cvv": "397"
  }
}
```

#### `calcDeliveryPathAsGeoJson`
This works the same way as calcDeliveryPath, except that it returns a GeoJson string that can be visualised at geojson.io
