package org.example;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
@Getter
@Setter
public class DocumentManager {


    private HashMap<String,Document> documents;

    public DocumentManager() {
        this.documents = new HashMap<>();
    }

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId()==null) {
            document.setId(generateId());
            documents.put(document.getId(),document);
        }else {
           Document documentTmp = documents.get(document.getId());
           document.setCreated(documentTmp.getCreated());
           documents.put(document.getId(),document);
            }

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {

        List<Document> documentsByRequest =  new ArrayList<>();
        for (Map.Entry<String,Document> entry:documents.entrySet()) {
            if (check(request,entry.getValue())) {
                documentsByRequest.add(entry.getValue());
            }
        }

        return documentsByRequest;
    }
    public boolean check(SearchRequest request,Document document) {
        boolean matches = true;
            if (request.titlePrefixes != null) {
               matches = request.titlePrefixes.stream().anyMatch(prefix->document.getTitle().startsWith(prefix));
            }
            if (request.containsContents != null && matches) {
                matches = request.containsContents.stream().anyMatch(content->document.getContent().contains(content));
            }

            if (request.authorIds != null && matches) {
                matches = request.authorIds.stream().anyMatch(authorId->document.getAuthor().getId().equals(authorId));
            }
            if (request.createdFrom != null && matches) {
                matches = request.createdFrom.isBefore(document.created) || request.createdFrom == document.created;
            }
            if (request.createdTo != null && matches) {
                matches = request.createdTo.isAfter(document.created) || request.createdTo == document.created;

            }
        return matches;
    }
    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    public String generateId(){
       return UUID.randomUUID().toString();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;

    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
