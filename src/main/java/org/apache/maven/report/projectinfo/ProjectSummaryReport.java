package org.apache.maven.report.projectinfo;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.model.Organization;
import org.codehaus.plexus.i18n.I18N;

import java.util.Locale;

/**
 * @goal summary
 * @plexus.component
 *
 * @author Edwin Punzalan
 */
public class ProjectSummaryReport
    extends AbstractMavenReport
{
    /**
     * Report output directory.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    private String outputDirectory;

    /**
     * Doxia Site Renderer.
     *
     * @component
     */
    private Renderer siteRenderer;

    /**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Internationalization.
     *
     * @component
     */
    private I18N i18n;

    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        new ProjectSummaryRenderer( getSink(), locale ).render();
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    public String getName( Locale locale )
    {
        return i18n.getString( "project-info-report", locale, "report.summary.name" );
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getCategoryName()
     */
    public String getCategoryName()
    {
        return CATEGORY_PROJECT_INFORMATION;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    public String getDescription( Locale locale )
    {
        return i18n.getString( "project-info-report", locale, "report.summary.description" );
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    protected String getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    protected MavenProject getProject()
    {
        return project;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
     */
    protected Renderer getSiteRenderer()
    {
        return siteRenderer;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    public String getOutputName()
    {
        return "project-summary";
    }

    private class ProjectSummaryRenderer
        extends AbstractMavenReportRenderer
    {
        private Locale locale;

        public ProjectSummaryRenderer( Sink sink, Locale locale )
        {
            super( sink );

            this.locale = locale;
        }

        public String getTitle()
        {
            return getReportString( "report.summary.title" );
        }

        protected void renderBody()
        {
            startSection( getTitle() );

            //generatl information sub-section
            String name = project.getName();
            if ( name == null )
            {
                name = "";
            }
            String description = project.getDescription();
            if ( description == null )
            {
                description = "";
            }
            String homepage = project.getUrl();
            if ( homepage == null )
            {
                homepage = "";
            }

            startSection( getReportString( "report.summary.general.title" ) );
            startTable();
            tableHeader( new String[] { getReportString( "Field" ), getReportString( "Value" ) } );
            tableRow( new String[] { getReportString( "report.summary.general.name" ), name } );
            tableRow( new String[] { getReportString( "report.summary.general.description" ), description } );
            tableRowWithLink( new String[] { getReportString( "report.summary.general.homepage" ), homepage } );
            endTable();
            endSection();

            //organization sub-section
            startSection( getReportString( "report.summary.organization.title" ) );
            Organization organization = project.getOrganization();
            if ( organization == null )
            {
                paragraph( "This project does not belong to an organization." );
            }
            else
            {
                if ( organization.getName() == null )
                {
                    organization.setName( "" );
                }
                if ( organization.getUrl() == null )
                {
                    organization.setUrl( "") ;
                }

                startTable();
                tableHeader( new String[] { getReportString( "Field" ), getReportString( "Value" ) } );
                tableRow( new String[] { getReportString( "report.summary.organization.name" ), organization.getName() } );
                tableRowWithLink( new String[] { getReportString( "report.summary.organization.url" ), organization.getUrl() } );
                endTable();
            }
            endSection();

            //build section
            startSection( getReportString( "report.summary.build.title" ) );
            startTable();
            tableHeader( new String[] { getReportString( "Field" ), getReportString( "Value" ) } );
            tableRow( new String[] { getReportString( "report.summary.build.groupid" ), project.getGroupId() } );
            tableRow( new String[] { getReportString( "report.summary.build.artifactid" ), project.getArtifactId() } );
            tableRow( new String[] { getReportString( "report.summary.build.version" ), project.getVersion() } );
            tableRow( new String[] { getReportString( "report.summary.build.type" ), project.getPackaging() } );
            endTable();
            endSection();

            endSection();
        }

        private String getReportString( String key )
        {
            return i18n.getString( "project-info-report", locale, key );
        }

        private void tableRowWithLink( String[] content )
        {
            sink.tableRow();

            for ( int ctr = 0; ctr < content.length; ctr++ )
            {
                String cell = content[ ctr ];
                if ( cell == null )
                {
                    cell = "";
                }

                sink.tableCell();

                if ( ctr == content.length -1 && cell.length() > 0 )
                {
                    sink.link( cell );
                    sink.text( cell );
                    sink.link_();
                }
                else
                {
                    sink.text( cell );
                }

                sink.tableCell_();
            }

            sink.tableRow_();
        }
    }
}
