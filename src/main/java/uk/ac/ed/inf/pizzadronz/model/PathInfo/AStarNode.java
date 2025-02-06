package uk.ac.ed.inf.pizzadronz.model.PathInfo;

/**
 * Node class to store position and cost info used to find path in A* algorithm.
  */
public class AStarNode {
    LngLat position;
    double g; // Cost from start to this node
    double f; // Total cost

    AStarNode(LngLat position, double g, double f) {
        this.position = position;
        this.g = g;
        this.f = f;
    }
}
