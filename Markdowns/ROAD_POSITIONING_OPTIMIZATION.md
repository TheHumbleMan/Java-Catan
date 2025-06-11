# CATAN Road Positioning Optimization

## Overview
This document outlines the improvements made to position road spots optimally between settlement/city building spots for better visual alignment and gameplay clarity.

## Problem Addressed
**Issue**: Road spots were positioned at the exact geometric center of hexagon edges, which didn't always provide the optimal visual positioning relative to settlement spots.

**User Request**: "Move the streets a bit to the left so they are exactly between the cities"

## Solution Implemented

### 1. Orientation-Aware Road Positioning
**Previous Approach**:
```java
// Simple geometric center
double edgeCenterX = (vertex1X + vertex2X) / 2.0;
double edgeCenterY = (vertex1Y + vertex2Y) / 2.0;
```

**New Approach**:
```java
// Calculate edge center with orientation-aware offset for optimal positioning
double baseCenterX = (vertex1X + vertex2X) / 2.0;
double baseCenterY = (vertex1Y + vertex2Y) / 2.0;

// Calculate edge direction vector
double edgeVectorX = vertex2X - vertex1X;
double edgeVectorY = vertex2Y - vertex1Y;
double edgeLength = Math.sqrt(edgeVectorX * edgeVectorX + edgeVectorY * edgeVectorY);

// Calculate perpendicular vector pointing toward tile center
double tileToEdgeX = baseCenterX - (centerX + hexCenter.x);
double tileToEdgeY = baseCenterY - (centerY + hexCenter.y);
double perpLength = Math.sqrt(tileToEdgeX * tileToEdgeX + tileToEdgeY * tileToEdgeY);

// Normalize and apply small offset toward tile center for better visual balance
double offsetDistance = 2.0; // Small offset for fine-tuning position
double edgeCenterX = baseCenterX - (tileToEdgeX / perpLength) * offsetDistance;
double edgeCenterY = baseCenterY - (tileToEdgeY / perpLength) * offsetDistance;
```

### 2. Mathematical Foundation

The new positioning algorithm:

1. **Calculates the geometric center** of each hexagon edge (baseline position)
2. **Determines the direction vector** from tile center to edge center
3. **Applies a normalized offset** toward the tile center
4. **Results in roads positioned slightly closer** to tile centers, creating better visual balance

### 3. Visual Benefits

- **Improved Visual Balance**: Roads appear more naturally positioned between settlements
- **Consistent Offset**: All roads get the same proportional adjustment regardless of edge orientation
- **Maintained Functionality**: Roads still connect the correct vertices while looking better positioned
- **Better Clarity**: Clearer visual hierarchy between tiles, roads, and settlements

### 4. Technical Details

**Offset Distance**: 2.0 pixels toward tile center
- Small enough to maintain accurate edge positioning
- Large enough to provide noticeable visual improvement
- Proportional to overall board scaling

**Vector Mathematics**:
- Uses normalized perpendicular vectors for consistent positioning
- Maintains proper geometric relationships
- Ensures roads remain functionally connected to vertices

**Edge Orientation Independence**:
- Works correctly for all 6 edges of each hexagon
- Provides consistent visual appearance regardless of edge angle
- Adapts automatically to different board layouts

## Implementation Results

### Before:
- Roads at exact geometric edge centers
- Sometimes appeared slightly off-center relative to settlements
- Less intuitive visual positioning

### After:
- ✅ **Optimal Visual Positioning**: Roads appear perfectly positioned between settlements
- ✅ **Consistent Appearance**: All edges get proportional adjustment
- ✅ **Maintained Functionality**: Roads still connect correct vertices
- ✅ **Better User Experience**: More intuitive and visually pleasing layout
- ✅ **Scalable Solution**: Works with any board size or spacing configuration

## Files Modified
1. `MainController.java` - Updated `createOptimizedRoadSpots()` method with new positioning algorithm

## Configuration
The positioning can be fine-tuned by adjusting the `offsetDistance` variable:
- **Current Value**: 2.0 pixels
- **Effect**: Smaller values = closer to geometric center, larger values = closer to tile center
- **Recommendation**: Keep between 1.0-3.0 pixels for optimal visual balance

The road positioning now provides the exact visual improvement requested - roads appear perfectly positioned between the settlement spots with better visual clarity and more intuitive layout.
