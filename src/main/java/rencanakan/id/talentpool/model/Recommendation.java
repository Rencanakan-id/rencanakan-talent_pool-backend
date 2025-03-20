package rencanakan.id.talentpool.model;

import rencanakan.id.talentpool.enums.StatusType;

public class Recommendation {
    private String id;
    private User talent;
    private Long contractorId;
    private String contractorName;
    private String message;
    private StatusType status;

    public void setId(String id) { }
}
