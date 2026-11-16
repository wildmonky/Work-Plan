import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_5eda3834083493fa818d6345ef830d95
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('datalist.html?dataname=sys_table_column&find_table_id="+mapRow.get("TABLE_ID")+"&find_table_column_group_id="+mapRow.get("TABLE_COLUMN_GROUP_ID")+"');";
return url;

    }
}
