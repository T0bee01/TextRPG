import java.io.*;
import java.util.*;

public class TextRPG {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // Player stats
        int playerHealth = 100;
        int playerAttack = 25;
        int playerLevel = 1;
        int playerXP = 0;
        int xpToLevelUp = 50;
        int potions = 3;

        // Enemy stats
        String[] enemies = {"Goblin", "Orc", "Troll", "Skeleton"};
        int maxEnemyHealth = 50;
        int enemyAttack = 15;

        // Inventory
        HashMap<String, Integer> inventory = new HashMap<>();

        System.out.println("Welcome to the Expanded Text-Based RPG!");
        System.out.println("Do you want to (1) Start a new game or (2) Load a saved game?");
        int choice = scanner.nextInt();

        // Load game logic
        if (choice == 2) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("savegame.dat"))) {
                playerHealth = ois.readInt();
                playerAttack = ois.readInt();
                playerLevel = ois.readInt();
                playerXP = ois.readInt();
                xpToLevelUp = ois.readInt();
                potions = ois.readInt();
                @SuppressWarnings("unchecked")
				HashMap<String, Integer> tempInventory = (HashMap<String, Integer>) ois.readObject();
				inventory = tempInventory; // Ensures proper type checking

                System.out.println("Game loaded successfully!");
            } catch (Exception e) {
                System.out.println("Failed to load the game. Starting a new game...");
            }
        }

        // Main game loop
        while (true) {
            System.out.println("\n--- What would you like to do? ---");
            System.out.println("1. Explore");
            System.out.println("2. View Stats");
            System.out.println("3. View Inventory");
            System.out.println("4. Save and Exit");

            int action = scanner.nextInt();

            if (action == 1) { // Explore
                int event = random.nextInt(3); // Random event
                if (event == 0) { // Found a chest
                    String[] loot = {"Sword", "Shield", "Potion"};
                    String treasure = loot[random.nextInt(loot.length)];
                    inventory.put(treasure, inventory.getOrDefault(treasure, 0) + 1);
                    System.out.println("You found a chest and got a " + treasure + "!");
                } else if (event == 1) { // Enemy encounter
                    System.out.println("An enemy appears!");
                    String enemy = enemies[random.nextInt(enemies.length)];
                    int enemyHealth = random.nextInt(maxEnemyHealth - 20) + 20;
                    System.out.println("It's a " + enemy + " with " + enemyHealth + " HP!");

                    while (enemyHealth > 0) {
                        System.out.println("\nYour HP: " + playerHealth);
                        System.out.println("Enemy HP: " + enemyHealth);
                        System.out.println("Potions: " + potions);
                        System.out.println("1. Attack");
                        System.out.println("2. Drink Potion");
                        System.out.println("3. Run");

                        int battleChoice = scanner.nextInt();

                        if (battleChoice == 1) { // Attack
                            int damageDealt = random.nextInt(playerAttack - 10) + 10;
                            int damageTaken = random.nextInt(enemyAttack - 5) + 5;
                            enemyHealth -= damageDealt;
                            playerHealth -= damageTaken;
                            System.out.println("You dealt " + damageDealt + " damage.");
                            System.out.println("You took " + damageTaken + " damage.");
                        } else if (battleChoice == 2) { // Drink potion
                            if (potions > 0) {
                                playerHealth += 30;
                                potions--;
                                System.out.println("You drank a potion and restored 30 HP.");
                            } else {
                                System.out.println("You have no potions left!");
                            }
                        } else if (battleChoice == 3) { // Run
                            System.out.println("You ran away!");
                            break;
                        }

                        if (playerHealth <= 0) {
                            System.out.println("You have been defeated! Game Over.");
                            return;
                        }
                        if (enemyHealth <= 0) {
                            System.out.println("You defeated the " + enemy + "!");
                            int xpEarned = random.nextInt(30) + 20;
                            playerXP += xpEarned;
                            System.out.println("You earned " + xpEarned + " XP. Total XP: " + playerXP + "/" + xpToLevelUp);

                            if (playerXP >= xpToLevelUp) {
                                playerLevel++;
                                playerXP -= xpToLevelUp;
                                xpToLevelUp += 50;
                                playerHealth += 20;
                                playerAttack += 5;
                                System.out.println("You leveled up to Level " + playerLevel + "!");
                                System.out.println("HP and Attack increased!");
                            }
                        }
                    }
                } else { // Resting
                    System.out.println("You found a peaceful spot and restored 20 HP.");
                    playerHealth = Math.min(playerHealth + 20, 100);
                }

            } else if (action == 2) { // View Stats
                System.out.println("\n--- Player Stats ---");
                System.out.println("Level: " + playerLevel);
                System.out.println("HP: " + playerHealth);
                System.out.println("Attack: " + playerAttack);
                System.out.println("XP: " + playerXP + "/" + xpToLevelUp);
                System.out.println("Potions: " + potions);

            } else if (action == 3) { // View Inventory
                System.out.println("\n--- Inventory ---");
                inventory.forEach((item, count) -> System.out.println(item + ": " + count));

            } else if (action == 4) { // Save and Exit
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("savegame.dat"))) {
                    oos.writeInt(playerHealth);
                    oos.writeInt(playerAttack);
                    oos.writeInt(playerLevel);
                    oos.writeInt(playerXP);
                    oos.writeInt(xpToLevelUp);
                    oos.writeInt(potions);
                    oos.writeObject(inventory);
                    System.out.println("Game saved successfully! Exiting...");
                } catch (IOException e) {
                    System.out.println("Failed to save the game.");
                }
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }

        scanner.close();
    }
}
