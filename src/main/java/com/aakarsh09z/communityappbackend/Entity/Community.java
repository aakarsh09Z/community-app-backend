
    package com.aakarsh09z.communityappbackend.Entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.hibernate.annotations.GenericGenerator;

    import java.util.List;

    @Entity
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "community")
    public class Community {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String description;
        private String icon;
        private Long adminId;
        private Integer membersNumber;
        @JsonIgnore
        @ManyToMany(cascade = {CascadeType.ALL})
        @JoinTable(
                name = "community_members",
                joinColumns = @JoinColumn(name = "community_id"),
                inverseJoinColumns = @JoinColumn(name = "user_id")
        )
        private List<User> members;
        @JsonIgnore
        @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Channel> channels;
        @JsonIgnore
        @OneToMany(mappedBy="community", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Post> posts;
    }
