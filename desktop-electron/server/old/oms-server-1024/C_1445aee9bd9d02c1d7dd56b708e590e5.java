import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_1445aee9bd9d02c1d7dd56b708e590e5
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "1".equals(mapRow.get("MODIFY_FLAG_YEAR"))?"<font color=red>"+mapRow.get("YEAR_OCS_DL")+"</font>":"mapRow.get("YEAR_OCS_DL")";
    }
}
