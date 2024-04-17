package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.locks.*;

public class ProductMonitor {
    Queue<Item> available;
    Queue<Item> withdrawn;
    private ReentrantLock lock; // Creating this lock object for thread synchronization

    public ProductMonitor() {
        available = new LinkedList<>();
        withdrawn = new LinkedList<>();
        lock = new ReentrantLock(); // Creates an instance for ReentrantLock
    }

    public void removeItemsFromUnavailability(Collection<Item> cls) {
        lock.lock(); // Acquire the lock
        try {
            for (Item x : cls) {
                if (withdrawn.remove(x))
                    available.add(x); // Adds the item to the available queue if it's removed from the withdrawn queue
            }
        } finally {
            lock.unlock(); // Attempts to release the lock
        }
    }

    public Optional<Item> getAvailableItem() {
        lock.lock();
        try{
            Optional<Item> o = Optional.empty();
            if (!available.isEmpty()) {
                var obj = available.remove();
                if (obj != null) {
                    o = Optional.of(obj); // Returns the removed item from the available queue
                    withdrawn.add(o.get()); // Adds the removed item to the withdrawn queue
                }
            }
            return o;
        } finally {
            lock.unlock();
        }
    }

    public boolean doShelf(Item u) {
        lock.lock();
        try {
            boolean result = false;
            if (withdrawn.remove(u)) {
                available.add(u);// Adds the item to the available queue if it's removed from the withdrawn queue
                result = true;
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    public Set<String> getAvailableItems() {
        lock.lock();
        try {
            Set<String> s;
            s = available.stream().map(x -> x.productName).collect(Collectors.toSet());
            return s;
        } finally {
            lock.unlock();
        }
    }

    public void addAvailableProduct(Item x) {
        lock.lock();
        try {
            available.add(x); // Adds the item to the available queue
        } finally {
            lock.unlock();
        }
    }

    public double updatePurchase(Double aDouble,
                                 List<Item> toIterate,
                                 List<Item> currentlyPurchasable,
                                 List<Item> currentlyUnavailable) {
        lock.lock();
        try {
            double total_cost = 0.0;
            for (var x : toIterate) {
                if (withdrawn.contains(x)) {
                    currentlyPurchasable.add(x); // Adds the item to currentlyPurchasable if it is in the withdrawn queue
                    total_cost += aDouble; //  Increments the total cost with aDouble
                } else {
                    currentlyUnavailable.add(x); // Adds the item to currentlyUnavailable if it's not in the withdrawn queue
                }
            }
            return total_cost;
        } finally {
            lock.unlock();
        }
    }

    public void makeAvailable(List<Item> toIterate) {
        lock.lock();
        try {
            for (var x : toIterate) {
                if (withdrawn.remove(x)) {
                    available.add(x);// Adds the item to the available queue if it's removed from the withdrawn queue
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean completelyRemove(List<Item> toIterate) {
        lock.lock();
        try {
            boolean allEmpty;
            for (var x : toIterate) {
                withdrawn.remove(x); // Removes the item from the withdrawn queue
                available.remove(x); // Removes the item from the available queue
            }
            allEmpty = withdrawn.isEmpty() && available.isEmpty();
            return allEmpty;
        } finally {
            lock.unlock();
        }
    }
}
