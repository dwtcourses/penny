package com.ejsfbu.app_main.Models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Reward")
public class Reward extends ParseObject {

    public static final String KEY_NAME = "rewardName";
    public static final String KEY_BADGE_IMAGE = "badgeImage";
    public static final String KEY_DESCRIPTION = "rewardDescription";
    public static final String KEY_IN_PROGRESS = "inProgress";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_LEVEL_1 = "isLevel1";
    public static final String KEY_HAS_DISCOUNT = "hasDiscount";
    public static final String KEY_HAS_GIFT_CARD = "hasGiftCard";
    public static final String KEY_DISCOUNT_CODE = "discountCode";
    public static final String KEY_GIFT_CARD_CODE = "giftCardCode";


    public String getName() {
        String name;
        try {
            name = fetchIfNeeded().getString(KEY_NAME);
        } catch (ParseException e) {
            e.printStackTrace();
            name = "";
        }
        return name;
    }

    public ParseFile getBadgeImage() {
        ParseFile image = null;
        try {
            image = fetchIfNeeded().getParseFile(KEY_BADGE_IMAGE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return image;
    }

    public String getDescription() {
        String description = "";
        try {
            description = fetchIfNeeded().getString(KEY_DESCRIPTION);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return description;
    }

    public boolean hasDiscount() {
        boolean has = false;
        try {
            has = fetchIfNeeded().getBoolean(KEY_HAS_DISCOUNT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return has;
    }

    public String getDiscountCode() {
        String code = "";
        try {
            code = fetchIfNeeded().getString(KEY_DISCOUNT_CODE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean hasGiftCard() {
        boolean has = false;
        try {
            has = fetchIfNeeded().getBoolean(KEY_HAS_GIFT_CARD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return has;
    }

    public String getGiftCardCode() {
        String code = "";
        try {
            code = fetchIfNeeded().getString(KEY_GIFT_CARD_CODE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static List<Reward> checkEarnedRewards(User user) {
        ArrayList<Reward> earnedRewards = new ArrayList<>();

        Reward completedGoalsReward = checkCompletedGoals(user);
        if (completedGoalsReward != null) {
            earnedRewards.add(completedGoalsReward);
        }

        Reward totalSavedReward = checkEarnedTotalSavedBadge(user);
        if (totalSavedReward != null) {
            earnedRewards.add(totalSavedReward);
        }

        Reward completedEarlyReward = checkEarlyBadges(user);
        if (completedEarlyReward != null) {
            earnedRewards.add(completedEarlyReward);
        }

        Reward smallGoalReward = checkSmallGoals(user);
        if (smallGoalReward != null) {
            earnedRewards.add(smallGoalReward);
        }

        Reward mediumGoalReward = checkMediumGoals(user);
        if (mediumGoalReward != null) {
            earnedRewards.add(mediumGoalReward);
        }

        Reward bigGoalReward = checkBigGoals(user);
        if (bigGoalReward != null) {
            earnedRewards.add(bigGoalReward);
        }

        return earnedRewards;
    }

    public static Reward checkCompletedGoals(User user) {
        int numberCompleted = user.getNumberGoalsCompleted();
        List<Reward> rewards = getGoalGroupBadges();
        List<Reward> completed = user.getCompletedBadges();
        Reward earnedReward;
        if (numberCompleted >= 50 && !userHasBadge(user, rewards.get(4).getObjectId())) {
            return null;
        } else if (numberCompleted >= 50) {
            if (!userHasBadge(user, rewards.get(4).getObjectId())) {
                user.addCompletedBadge(rewards.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(rewards.get(3));
                            user.removeInProgressBadge(rewards.get(4));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(4);
            } else {
                earnedReward = null;
            }
        } else if (numberCompleted >= 25) {
            if (!userHasBadge(user, rewards.get(3).getObjectId())) {
                user.addCompletedBadge(rewards.get(3));
                user.addInProgressBadge(rewards.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(rewards.get(2));
                            user.removeInProgressBadge(rewards.get(3));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(3);
            } else {
                earnedReward = null;
            }
        } else if (numberCompleted >= 10) {
            if (!userHasBadge(user, rewards.get(2).getObjectId())) {
                user.addCompletedBadge(rewards.get(2));
                user.addInProgressBadge(rewards.get(3));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(rewards.get(1));
                            user.removeInProgressBadge(rewards.get(2));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(2);
            } else {
                earnedReward = null;
            }
        } else if (numberCompleted >= 5) {
            if (!userHasBadge(user, rewards.get(1).getObjectId())) {
                user.addCompletedBadge(rewards.get(1));
                user.addInProgressBadge(rewards.get(2));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(rewards.get(0));
                            user.removeInProgressBadge(rewards.get(1));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(1);
            } else {
                earnedReward = null;
            }
        } else if (numberCompleted >= 1) {
            if (!userHasBadge(user, rewards.get(0).getObjectId())) {
                user.addInProgressBadge(rewards.get(1));
                user.addCompletedBadge(rewards.get(0));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(rewards.get(0));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(0);
            } else {
                earnedReward = null;
            }
        } else {
            earnedReward = null;
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        return earnedReward;
    }

    public static Reward checkEarlyBadges(User user) {
        int numEarly = user.getEarlyGoals();
        List<Reward> rewards = getCompletedEarlyBadges();
        Reward earnedReward;
        if (numEarly >= 50) {
            if (userHasBadge(user, rewards.get(4).getObjectId())) {
                earnedReward = null;
            } else {
                user.addCompletedBadge(rewards.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(rewards.get(3));
                            user.removeInProgressBadge(rewards.get(4));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(4);
            }
        } else if (numEarly >= 25) {
            if (userHasBadge(user, rewards.get(3).getObjectId())) {
                earnedReward = null;
            } else {
                user.addCompletedBadge(rewards.get(3));
                user.addInProgressBadge(rewards.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(rewards.get(2));
                            user.removeInProgressBadge(rewards.get(3));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(3);
            }
        } else if (numEarly >= 10) {
            if (userHasBadge(user, rewards.get(2).getObjectId())) {
                earnedReward = null;
            } else {
                user.addCompletedBadge(rewards.get(2));
                user.addInProgressBadge(rewards.get(3));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(rewards.get(1));
                            user.removeInProgressBadge(rewards.get(2));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(2);
            }
        } else if (numEarly >= 5) {
            if (userHasBadge(user, rewards.get(1).getObjectId())) {
                earnedReward = null;
            } else {
                user.addCompletedBadge(rewards.get(1));
                user.addInProgressBadge(rewards.get(2));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(rewards.get(0));
                            user.removeInProgressBadge(rewards.get(1));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(1);
            }
        } else if (numEarly >= 1) {
            if (userHasBadge(user, rewards.get(0).getObjectId())) {
                earnedReward = null;
            } else {
                user.addCompletedBadge(rewards.get(0));
                user.addInProgressBadge(rewards.get(1));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(rewards.get(0));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedReward = rewards.get(0);
            }
        } else {
            earnedReward = null;
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        return earnedReward;
    }

    public static Reward checkEarnedTotalSavedBadge(User user) {
        Double totalSaved = user.getTotalSaved();
        ArrayList<Reward> totalSavedBadges = getTotalSavedBadges();
        Reward earnedBadge;
        if (totalSaved >= 1000) {
            if (userHasBadge(user, totalSavedBadges.get(4).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(totalSavedBadges.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(totalSavedBadges.get(3));
                            user.removeInProgressBadge(totalSavedBadges.get(4));
                        }
                    }
                });
                earnedBadge = totalSavedBadges.get(4);
            }
        } else if (totalSaved >= 500) {
            if (userHasBadge(user, totalSavedBadges.get(3).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(totalSavedBadges.get(3));
                user.addInProgressBadge(totalSavedBadges.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(totalSavedBadges.get(2));
                            user.removeInProgressBadge(totalSavedBadges.get(3));
                        }
                    }
                });
                earnedBadge = totalSavedBadges.get(3);
            }
        } else if (totalSaved >= 250) {
            if (userHasBadge(user, totalSavedBadges.get(2).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(totalSavedBadges.get(2));
                user.addInProgressBadge(totalSavedBadges.get(3));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(totalSavedBadges.get(1));
                            user.removeInProgressBadge(totalSavedBadges.get(2));
                        }
                    }
                });
                earnedBadge = totalSavedBadges.get(2);
            }
        } else if (totalSaved >= 100) {
            if (userHasBadge(user, totalSavedBadges.get(1).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(totalSavedBadges.get(1));
                user.addInProgressBadge(totalSavedBadges.get(2));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeCompletedBadge(totalSavedBadges.get(0));
                            user.removeInProgressBadge(totalSavedBadges.get(1));
                        }
                    }
                });
                earnedBadge = totalSavedBadges.get(1);
            }
        } else if (totalSaved >= 50) {
            if (userHasBadge(user, totalSavedBadges.get(0).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(totalSavedBadges.get(0));
                user.addInProgressBadge(totalSavedBadges.get(1));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(totalSavedBadges.get(0));
                        }
                    }
                });
                earnedBadge = totalSavedBadges.get(0);
            }
        } else {
            earnedBadge = null;
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        return earnedBadge;
    }

    public static boolean userHasBadge(User user, String rewardId) {
        List<Reward> completedBadges = user.getCompletedBadges();
        for (int i = 0; i < completedBadges.size(); i++) {
            if (completedBadges.get(i).getObjectId().equals(rewardId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean userHasClaimedBadge(User user, String rewardId) {
        List<Reward> completedBadges = user.getClaimedRewards();
        for (int i = 0; i < completedBadges.size(); i++) {
            if (completedBadges.get(i).getObjectId().equals(rewardId)) {
                return true;
            }
        }
        return false;
    }

    public static List<Reward> getGoalGroupBadges() {
        List<Reward> badges = new ArrayList<>();
        Reward.Query query = new Reward.Query();
        query.getGroup("Goals Completed");
        try {
            badges.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public static ArrayList<Reward> getTotalSavedBadges() {
        ArrayList<Reward> badges = new ArrayList<>();
        Query query = new Query();
        query.getGroup("Total Saved");
        try {
            badges.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public static ArrayList<Reward> getCompletedEarlyBadges() {
        ArrayList<Reward> badges = new ArrayList<>();
        Query query = new Query();
        query.getGroup("Completed Early");
        try {
            badges.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public static ArrayList<Reward> getLevel1Badges() {
        ArrayList<Reward> badges = new ArrayList<>();
        Query query = new Query();
        query.getLevel1();
        try {
            badges.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public static class Query extends ParseQuery<Reward> {

        public Query() {
            super(Reward.class);
        }

        public Query getGroup(String groupName) {
            whereEqualTo(KEY_GROUP, groupName);
            orderByAscending(KEY_NAME);
            return this;
        }

        public Query getLevel1() {
            whereEqualTo(KEY_IS_LEVEL_1, true);
            orderByAscending(KEY_NAME);
            return this;
        }
    }

    public static Reward checkSmallGoals(User user) {
        ArrayList<Reward> smallGoalBadges = new Reward().getSmallGoalsBadges();
        Reward earnedBadge = null;
        if (user.getSmallGoals() >= 50) {
            if (userHasBadge(user, smallGoalBadges.get(4).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(smallGoalBadges.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(smallGoalBadges.get(4));
                            user.removeCompletedBadge(smallGoalBadges.get(3));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = smallGoalBadges.get(4);
            }
        } else if (user.getSmallGoals() >= 25) {
            if (userHasBadge(user, smallGoalBadges.get(3).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(smallGoalBadges.get(3));
                user.addInProgressBadge(smallGoalBadges.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(smallGoalBadges.get(3));
                            user.removeCompletedBadge(smallGoalBadges.get(2));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = smallGoalBadges.get(3);
            }
        } else if (user.getSmallGoals() >= 10) {
            if (userHasBadge(user, smallGoalBadges.get(2).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(smallGoalBadges.get(2));
                user.addInProgressBadge(smallGoalBadges.get(3));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(smallGoalBadges.get(2));
                            user.removeCompletedBadge(smallGoalBadges.get(1));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = smallGoalBadges.get(2);
            }
        } else if (user.getSmallGoals() >= 5) {
            if (userHasBadge(user, smallGoalBadges.get(1).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(smallGoalBadges.get(1));
                user.addInProgressBadge(smallGoalBadges.get(2));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(smallGoalBadges.get(1));
                            user.removeCompletedBadge(smallGoalBadges.get(0));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = smallGoalBadges.get(1);
            }
        } else if (user.getSmallGoals() >= 1) {
            if (userHasBadge(user, smallGoalBadges.get(0).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(smallGoalBadges.get(0));
                user.addInProgressBadge(smallGoalBadges.get(1));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(smallGoalBadges.get(0));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = smallGoalBadges.get(0);
            }
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        return earnedBadge;
    }

    public ArrayList<Reward> getSmallGoalsBadges() {
        ArrayList<Reward> badges = new ArrayList<>();
        Reward.Query query = new Reward.Query();
        query.getGroup("Small Goals");
        try {
            badges.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public static Reward checkMediumGoals(User user) {
        ArrayList<Reward> mediumGoalBadges = new Reward().getMediumGoalsBadges();
        Reward earnedBadge = null;
        if (user.getMediumGoals() >= 50) {
            if (userHasBadge(user, mediumGoalBadges.get(4).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(mediumGoalBadges.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(mediumGoalBadges.get(4));
                            user.removeCompletedBadge(mediumGoalBadges.get(3));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = mediumGoalBadges.get(4);
            }
        } else if (user.getMediumGoals() >= 25) {
            if (userHasBadge(user, mediumGoalBadges.get(3).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(mediumGoalBadges.get(3));
                user.addInProgressBadge(mediumGoalBadges.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(mediumGoalBadges.get(3));
                            user.removeCompletedBadge(mediumGoalBadges.get(2));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = mediumGoalBadges.get(3);
            }
        } else if (user.getMediumGoals() >= 10) {
            if (userHasBadge(user, mediumGoalBadges.get(2).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(mediumGoalBadges.get(2));
                user.addInProgressBadge(mediumGoalBadges.get(3));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(mediumGoalBadges.get(2));
                            user.removeCompletedBadge(mediumGoalBadges.get(1));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = mediumGoalBadges.get(2);
            }
        } else if (user.getMediumGoals() >= 5) {
            if (userHasBadge(user, mediumGoalBadges.get(1).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(mediumGoalBadges.get(1));
                user.addInProgressBadge(mediumGoalBadges.get(2));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(mediumGoalBadges.get(1));
                            user.removeCompletedBadge(mediumGoalBadges.get(0));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = mediumGoalBadges.get(1);
            }
        } else if (user.getMediumGoals() >= 1) {
            if (userHasBadge(user, mediumGoalBadges.get(0).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(mediumGoalBadges.get(0));
                user.addInProgressBadge(mediumGoalBadges.get(1));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(mediumGoalBadges.get(0));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = mediumGoalBadges.get(0);
            }
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        return earnedBadge;
    }

    public ArrayList<Reward> getMediumGoalsBadges() {
        ArrayList<Reward> badges = new ArrayList<>();
        Reward.Query query = new Reward.Query();
        query.getGroup("Medium Goals");
        try {
            badges.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return badges;
    }


    public static Reward checkBigGoals(User user) {
        ArrayList<Reward> bigGoalBadges = new Reward().getBigGoalsBadges();
        Reward earnedBadge = null;
        if (user.getBigGoals() >= 50) {
            if (userHasBadge(user, bigGoalBadges.get(4).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(bigGoalBadges.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(bigGoalBadges.get(4));
                            user.removeCompletedBadge(bigGoalBadges.get(3));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = bigGoalBadges.get(4);
            }
        } else if (user.getBigGoals() >= 25) {
            if (userHasBadge(user, bigGoalBadges.get(3).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(bigGoalBadges.get(3));
                user.addInProgressBadge(bigGoalBadges.get(4));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(bigGoalBadges.get(3));
                            user.removeCompletedBadge(bigGoalBadges.get(2));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = bigGoalBadges.get(3);
            }
        } else if (user.getBigGoals() >= 10) {
            if (userHasBadge(user, bigGoalBadges.get(2).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(bigGoalBadges.get(2));
                user.addInProgressBadge(bigGoalBadges.get(3));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(bigGoalBadges.get(2));
                            user.removeCompletedBadge(bigGoalBadges.get(1));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = bigGoalBadges.get(2);
            }
        } else if (user.getBigGoals() >= 5) {
            if (userHasBadge(user, bigGoalBadges.get(1).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(bigGoalBadges.get(1));
                user.addInProgressBadge(bigGoalBadges.get(2));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(bigGoalBadges.get(1));
                            user.removeCompletedBadge(bigGoalBadges.get(0));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = bigGoalBadges.get(1);
            }
        } else if (user.getBigGoals() >= 1) {
            if (userHasBadge(user, bigGoalBadges.get(0).getObjectId())) {
                earnedBadge = null;
            } else {
                user.addCompletedBadge(bigGoalBadges.get(0));
                user.addInProgressBadge(bigGoalBadges.get(1));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.removeInProgressBadge(bigGoalBadges.get(0));
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
                earnedBadge = bigGoalBadges.get(0);
            }
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        return earnedBadge;
    }

    public ArrayList<Reward> getBigGoalsBadges() {
        ArrayList<Reward> badges = new ArrayList<>();
        Reward.Query query = new Reward.Query();
        query.getGroup("Big Goals");
        try {
            badges.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return badges;
    }
}
