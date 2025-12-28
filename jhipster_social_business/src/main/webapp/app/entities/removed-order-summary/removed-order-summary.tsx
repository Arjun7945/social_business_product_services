import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './removed-order-summary.reducer';

export const RemovedOrderSummary = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const removedOrderSummaryList = useAppSelector(state => state.removedOrderSummary.entities);
  const loading = useAppSelector(state => state.removedOrderSummary.loading);
  const totalItems = useAppSelector(state => state.removedOrderSummary.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="removed-order-summary-heading" data-cy="RemovedOrderSummaryHeading">
        <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.home.title">Removed Order Summaries</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/removed-order-summary/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.home.createLabel">
              Create new Removed Order Summary
            </Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {removedOrderSummaryList && removedOrderSummaryList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('userOriginalId')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.userOriginalId">User Original Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('userOriginalId')} />
                </th>
                <th className="hand" onClick={sort('userName')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.userName">User Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('userName')} />
                </th>
                <th className="hand" onClick={sort('userRole')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.userRole">User Role</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('userRole')} />
                </th>
                <th className="hand" onClick={sort('totalOrders')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.totalOrders">Total Orders</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalOrders')} />
                </th>
                <th className="hand" onClick={sort('totalAmount')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.totalAmount">Total Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalAmount')} />
                </th>
                <th className="hand" onClick={sort('firstInteractionAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.firstInteractionAt">
                    First Interaction At
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('firstInteractionAt')} />
                </th>
                <th className="hand" onClick={sort('lastInteractionAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.lastInteractionAt">Last Interaction At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastInteractionAt')} />
                </th>
                <th className="hand" onClick={sort('removedAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.removedAt">Removed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('removedAt')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {removedOrderSummaryList.map((removedOrderSummary, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/removed-order-summary/${removedOrderSummary.id}`} color="link" size="sm">
                      {removedOrderSummary.id}
                    </Button>
                  </td>
                  <td>{removedOrderSummary.userOriginalId}</td>
                  <td>{removedOrderSummary.userName}</td>
                  <td>
                    <Translate contentKey={`whatsappProductServiceProApp.UserRole.${removedOrderSummary.userRole}`} />
                  </td>
                  <td>{removedOrderSummary.totalOrders}</td>
                  <td>{removedOrderSummary.totalAmount}</td>
                  <td>
                    {removedOrderSummary.firstInteractionAt ? (
                      <TextFormat type="date" value={removedOrderSummary.firstInteractionAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {removedOrderSummary.lastInteractionAt ? (
                      <TextFormat type="date" value={removedOrderSummary.lastInteractionAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {removedOrderSummary.removedAt ? (
                      <TextFormat type="date" value={removedOrderSummary.removedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/removed-order-summary/${removedOrderSummary.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/removed-order-summary/${removedOrderSummary.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/removed-order-summary/${removedOrderSummary.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="whatsappProductServiceProApp.removedOrderSummary.home.notFound">
                No Removed Order Summaries found
              </Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={removedOrderSummaryList && removedOrderSummaryList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default RemovedOrderSummary;
