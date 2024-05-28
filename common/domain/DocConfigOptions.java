// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class DocConfigOptions extends GsonDto
{
    private final List<Subject> subjects;
    private final List<String> difficulties;
    private final List<String> parsingSpecs;
    private final List<Task> taskTypes;
    
    public DocConfigOptions(final List<Subject> subjects, final List<String> difficulties, final List<String> parsingSpecs, final List<Task> taskTypes) {
        this.subjects = subjects;
        this.difficulties = difficulties;
        this.parsingSpecs = parsingSpecs;
        this.taskTypes = taskTypes;
    }
    
    public List<Subject> getSubjects() {
        return this.subjects;
    }
    
    public List<String> getDifficulties() {
        return this.difficulties;
    }
    
    public List<String> getParsingSpecs() {
        return this.parsingSpecs;
    }
    
    public static class SubjectCategory extends GsonDto
    {
        private final String name;
        private final List<String> subcategories;
        
        public SubjectCategory(final String name, final List<String> subcategories) {
            this.name = name;
            this.subcategories = subcategories;
        }
        
        public String getName() {
            return this.name;
        }
        
        public List<String> getSubcategories() {
            return this.subcategories;
        }
    }
    
    public static class Subject extends GsonDto
    {
        private final String name;
        private final String shortName;
        private final List<SubjectCategory> categories;
        private int subjectId;
        private int parentId;
        
        public Subject(final String name, final String shortName, final List<SubjectCategory> categories) {
            this.subjectId = 0;
            this.name = name;
            this.shortName = shortName;
            this.categories = categories;
        }
        
        public Subject(final String name, final String shortName, final List<SubjectCategory> categories, final int id, final int parentId) {
            this(name, shortName, categories);
            this.subjectId = id;
            this.parentId = parentId;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getShortName() {
            return this.shortName;
        }
        
        public List<SubjectCategory> getCategories() {
            return this.categories;
        }
        
        public Subject withId(final int id, final int parentId) {
            return new Subject(this.name, this.shortName, this.categories, id, parentId);
        }
    }
}
